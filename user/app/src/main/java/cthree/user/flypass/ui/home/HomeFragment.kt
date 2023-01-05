package cthree.user.flypass.ui.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import androidx.work.WorkInfo
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.user.flypass.R
import cthree.user.flypass.adapter.HighlightTopicAdapter
import cthree.user.flypass.adapter.RecentSearchAdapter
import cthree.user.flypass.data.DummyData
import cthree.user.flypass.data.GoogleTokenRequest
import cthree.user.flypass.data.RecentSearch
import cthree.user.flypass.databinding.DialogProgressBarBinding
import cthree.user.flypass.databinding.FragmentHomeBinding
import cthree.user.flypass.models.login.LoginData
import cthree.user.flypass.ui.dialog.DialogCaller
import cthree.user.flypass.ui.intro.JoinMemberFragment
import cthree.user.flypass.utils.AlertButton
import cthree.user.flypass.utils.Constants
import cthree.user.flypass.utils.SessionManager
import cthree.user.flypass.utils.Utils
import cthree.user.flypass.viewmodels.AirportViewModel
import cthree.user.flypass.viewmodels.PreferencesViewModel
import cthree.user.flypass.viewmodels.RecentSearchViewModel
import cthree.user.flypass.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "HomeFragment"

@AndroidEntryPoint
class HomeFragment : Fragment(), MenuProvider {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var progressAlertDialogBuilder : MaterialAlertDialogBuilder
    private lateinit var progressAlertDialog        : AlertDialog
    private lateinit var sessionManager: SessionManager
    private val airportViewModel: AirportViewModel by viewModels()
    private val recentSearchViewModel: RecentSearchViewModel by viewModels()
    private val userVM: UserViewModel by viewModels()
    private val prefVM: PreferencesViewModel by viewModels()
    private val calDep: Calendar = Calendar.getInstance()
    private val calArr: Calendar = Calendar.getInstance()
    private lateinit var departIata: String
    private lateinit var arriveIata: String
    private var userToken: String?  = null
    private var departCity: String? = null
    private var arriveCity: String? = null
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private lateinit var dateField: String
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private val seatClassFragment = SeatClassFragment()
    private val passengerAmountFragment = PassengerAmountFragment()
    private var firstOpen: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: Started")
        sessionManager = SessionManager(requireContext())
        progressAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        onBackPressedCallback = requireActivity().onBackPressedDispatcher.addCallback(this) {
        }
        super.onCreate(savedInstanceState)
    }

    private val resolutionForResult = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        run {
            if(activityResult != null){
                Log.d(TAG, "Activity Result: ${activityResult.data.toString()}")
                val credential = oneTapClient.getSignInCredentialFromIntent(activityResult.data)
                Log.d(TAG, "Credential: ${credential.id}")
                val email = credential.id
                val idToken = credential.googleIdToken
                val username = credential.givenName
                val password = credential.password
                Log.d(TAG, "Got Email: $email")
                Log.d(TAG, "Got ID token -> $idToken")
                Log.d(TAG, "Got password -> $password")
                Log.d(TAG, "Got json -> $username")

                if(idToken != null) userVM.callGoogleIdTokenLogin(GoogleTokenRequest(idToken))
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: Started")
        initProgressDialog()
        setupToolbar()
        setViews()
        setAdapter()
        setOnClickEvent()
        setBottomNav()

//        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
        userVM.loginToken().observe(viewLifecycleOwner){
            if(it != null){
                val profile = Utils.decodeAccountToken(it)
                // save incoming data
                prefVM.saveToken(it)
                userToken = it
                prefVM.saveData(profile)
            }
        }

        prefVM.dataUser.observe(viewLifecycleOwner){
            if(it.token.isEmpty()){
                Log.d(TAG, "token: null")
                if(firstOpen){
                    val joinMemberFragment = JoinMemberFragment()
                    joinMemberFragment.show(requireActivity().supportFragmentManager.beginTransaction(), joinMemberFragment.tag)
                    firstOpen = !firstOpen
                }
            }else if(it.token.isNotEmpty()){
                userToken = it.token
            }
        }

        if(sessionManager.getIsFirstInstall()){
            Log.d(TAG, "onViewCreated: first")
            airportViewModel.fetchAirportData()
            airportViewModel.getAirportWorkerInfo().observe(viewLifecycleOwner){
                val workInfo = it[0]
                if(workInfo.state == WorkInfo.State.SUCCEEDED){
                    Log.d(TAG, "fetchAirport: Done")
                    sessionManager.setIsFirstInstall(false)
                }
            }
        }
    }

    private fun setOnClickEvent() {
        binding.flightPay.setOnClickListener {
            if(userToken != null) findNavController().navigate(R.id.action_homeFragment_to_flightPayFragment)
            else{
                DialogCaller(requireActivity())
                    .setTitle(R.string.non_member_title)
                    .setMessage(R.string.non_member_msg)
                    .setPrimaryButton(R.string.non_member_btn_login){ dialog, _ ->
                        run{
                            callLoginDialog()
                            dialog.dismiss()
                        }
                    }
                    .setSecondaryButton(R.string.maybe_later){ dialog, _ ->
                        run{
                            dialog.dismiss()
                        }
                    }
                    .create(layoutInflater, AlertButton.TWO)
                    .show()
            }
        }

        binding.topup.setOnClickListener {
            if(userToken != null) findNavController().navigate(R.id.action_homeFragment_to_topUpFragment)
            else{
                DialogCaller(requireActivity())
                    .setTitle(R.string.non_member_title)
                    .setMessage(R.string.non_member_msg)
                    .setPrimaryButton(R.string.non_member_btn_login){ dialog, _ ->
                        run{
                            callLoginDialog()
                            dialog.dismiss()
                        }
                    }
                    .setSecondaryButton(R.string.maybe_later){ dialog, _ ->
                        run{
                            dialog.dismiss()
                        }
                    }
                    .create(layoutInflater, AlertButton.TWO)
                    .show()
            }
        }

        binding.swRoundTrip.setOnClickListener {
            binding.etArriveDate.isVisible = binding.swRoundTrip.isChecked
            binding.tvArrivalDate.isVisible = binding.swRoundTrip.isChecked
        }

        // set date dialog listener
        val dateDepListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calDep.set(Calendar.YEAR, year)
                calDep.set(Calendar.MONTH, monthOfYear)
                calDep.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                // after set we have to update in view
                updateDate()
            }
        val dateArrListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calArr.set(Calendar.YEAR, year)
                calArr.set(Calendar.MONTH, monthOfYear)
                calArr.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                // after set we have to update in view
                updateDate()
            }

        binding.etDepartureDate.setOnClickListener {
            dateField = Constants.DEPART_DATE
            DatePickerDialog(
                requireActivity(),
                dateDepListener,
                calDep.get(Calendar.YEAR),
                calDep.get(Calendar.MONTH),
                calDep.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.etArriveDate.setOnClickListener {
            dateField = Constants.ARRIVE_DATE
            DatePickerDialog(
                requireActivity(),
                dateArrListener,
                calArr.get(Calendar.YEAR),
                calArr.get(Calendar.MONTH),
                calArr.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.btnSearch.setOnClickListener {
            navigateToTicketList()
            userVM.clearAirportSearch()
            sessionManager.setSeatClass(binding.etSeatClass.text.toString())
            sessionManager.setPassengerAmount(binding.etPassengers.text.toString().toInt())
        }

        binding.etFromAirport.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FLIGHT_DIR, Constants.DEPART_AIRPORT)
            Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_searchAirportFragment, bundle)
        }

        binding.etToAirport.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FLIGHT_DIR, Constants.ARRIVE_AIRPORT)
            Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_searchAirportFragment, bundle)
        }

        binding.etSeatClass.setOnClickListener {
            seatClassFragment.show(requireActivity().supportFragmentManager, seatClassFragment.tag)
        }

        seatClassFragment.setOnCLickListener(object : SeatClassFragment.OnClickListener{
            override fun onClick(seatClass: String) {
                binding.etSeatClass.setText(seatClass)
            }
        })

        binding.etPassengers.setOnClickListener {
            passengerAmountFragment.show(requireActivity().supportFragmentManager, passengerAmountFragment.tag)
        }
        passengerAmountFragment.setOnClickListener(object : PassengerAmountFragment.OnClickListener{
            override fun onClick(passenger: String) {
                binding.etPassengers.setText(passenger)
            }
        })

        binding.tvClearAll.setOnClickListener {
            recentSearchViewModel.deleteAllSearch()
        }
    }

    private fun setupToolbar() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setLogo(R.drawable.logo_toolbar)
    }

    private fun initProgressDialog(){
        val progressBarBinding = DialogProgressBarBinding.inflate(layoutInflater, null, false)
        progressAlertDialogBuilder.setView(progressBarBinding.root)

        progressAlertDialog = progressAlertDialogBuilder.create()
        progressAlertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        progressAlertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
    }


    private fun navigateToTicketList() {
        val dateDep = Utils.reverseDateFormat(calDep)
        val dateArr = if(binding.etArriveDate.isVisible) Utils.reverseDateFormat(calArr) else null
        val arriveDateTv = if(binding.swRoundTrip.isChecked) binding.etArriveDate.text.toString() else null
        val recentSearch = RecentSearch(
            departDate = dateDep,
            arriveDate = dateArr,
            iataArriveAirport = arriveIata,
            iataDepartAirport = departIata,
            arriveCity = arriveCity ?: "City",
            departCity = departCity ?: "City",
            id = 0,
            seatClass = binding.etSeatClass.text.toString(),
            passengerAmount = binding.etPassengers.text.toString().toInt()
        )
        val directions = HomeFragmentDirections.actionHomeFragmentToTicketListFragment(
            search = recentSearch,
            arrDateTv = arriveDateTv,
            depDateTv = binding.etDepartureDate.text.toString(),
            isRoundtrip = binding.swRoundTrip.isChecked
        )
        recentSearchViewModel.insertSearch(recentSearch)
        findNavController().navigate(directions)
    }

    @SuppressLint("SetTextI18n")
    private fun setViews() {
        userVM.dataUser.observe(viewLifecycleOwner){
            if(it.arriveAirportCity.isNotEmpty()){
                arriveIata = it.arriveAirportIata
                arriveCity = it.arriveAirportCity
                binding.etToAirport.setText("${it.arriveAirportCity}, ${it.arriveAirportCountry}")
            }
            if(it.departAirportCity.isNotEmpty()){
                departIata = it.departAirportIata
                departCity = it.departAirportCity
                binding.etFromAirport.setText("${it.departAirportCity}, ${it.departAirportCountry}")
            }
        }
    }

    private fun setAdapter() {
        with(binding.vpHighlight){
            adapter = HighlightTopicAdapter(DummyData.highlightTopicItem)
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            offscreenPageLimit = 3
            setPageTransformer(MarginPageTransformer(50))
            binding.wormDot.attachTo(binding.vpHighlight)
            clipToPadding = false
            setPadding(10, 10, 10 ,0)
        }
        val recentAdapter = RecentSearchAdapter()
        recentSearchViewModel.getAllSearch().observe(viewLifecycleOwner){
            if(it.isEmpty()){
                recentAdapter.submitList(emptyList())
                binding.layoutRecentSearch.isVisible = false
            }else{
                recentAdapter.submitList(it)
                binding.layoutRecentSearch.isVisible = true
            }
        }
        with(binding.rvRecentSearch){
            adapter = recentAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            recentAdapter.setOnItemClickListener(object : RecentSearchAdapter.SetOnItemClickListener{
                override fun onItemClick(search: RecentSearch) {
                    Log.d(TAG, "search: $search")
                    val directions = HomeFragmentDirections.actionHomeFragmentToTicketListFragment(
                        search = search,
                        arrDateTv = search.arriveDate,
                        depDateTv = search.departDate,
                        isRoundtrip = search.arriveDate != null
                    )
                    findNavController().navigate(directions)
                }
            })
        }
    }

    private fun updateDate() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        if(dateField == Constants.DEPART_DATE)
            binding.etDepartureDate.setText(sdf.format(calDep.time))
        else
            binding.etArriveDate.setText(sdf.format(calArr.time))
    }

    private fun setBottomNav(){
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.isVisible = true
    }

    private fun callLoginDialog() {
        DialogCaller(requireActivity())
            .setTitle(R.string.login_dialog_title)
            .setLoginButton(R.string.login_dialog_login_btn, object : DialogCaller.OnClickLoginListener{
                override fun onClick(dialog: DialogInterface, email: String?, password: String?) {
                    if(email != null && password != null){
                        userVM.callLoginUser(LoginData(email, password))
                        dialog.dismiss()
                        progressAlertDialog.show()
                    }
                }
            })
            .setGoogleButton(R.string.login_dialog_google_btn, object : DialogCaller.OnClickGoogleListener{
                override fun onClick(dialog: DialogInterface, email: String?, password: String?) {
                    oneTapClient.beginSignIn(signInRequest)
                        .addOnCompleteListener(requireActivity()){ result ->
                            try {
                                Log.d(TAG, "Login Success: ${result.result.pendingIntent}")
                                val intentSenderRequest = IntentSenderRequest.Builder(result.result.pendingIntent).build()
                                resolutionForResult.launch(intentSenderRequest)
                            }catch (e: IntentSender.SendIntentException) {
                                Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                            }
                        }
                        .addOnFailureListener(requireActivity()) { e ->
                            // No saved credentials found. Launch the One Tap sign-up flow, or
                            // do nothing and continue presenting the signed-out UI.
                            Log.d(TAG, e.localizedMessage)
                        }
                }
            })
            .create(layoutInflater, AlertButton.LOGIN)
            .show()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.home_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        Log.d(TAG, "onMenuItemSelected: ")
        return when (menuItem.itemId){
            R.id.notification ->{
                Log.d(TAG, "onMenuItemSelected: Clicked")
                checkUserMember()
                true
            }
            else -> false
        }
    }

    private fun checkUserMember() {
        prefVM.dataUser.observe(viewLifecycleOwner) {
            if (it.token.isNotEmpty()) {
                findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
            } else {
                DialogCaller(requireActivity())
                    .setTitle(R.string.non_member_title)
                    .setMessage(R.string.non_member_msg)
                    .setPrimaryButton(R.string.non_member_btn_login){ dialog, _ ->
                        run{
                            callLoginDialog()
                            dialog.dismiss()
                        }
                    }
                    .setSecondaryButton(R.string.maybe_later){ dialog, _ ->
                        run{
                            dialog.dismiss()
                        }
                    }
                    .create(layoutInflater, AlertButton.TWO)
                    .show()
            }
        }
    }

}