package cthree.user.flypass.ui.booking

import android.content.DialogInterface
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.user.flypass.R
import cthree.user.flypass.data.GoogleTokenRequest
import cthree.user.flypass.databinding.DialogProgressBarBinding
import cthree.user.flypass.databinding.FragmentFilghtConfirmationBinding
import cthree.user.flypass.models.flight.Flight
import cthree.user.flypass.models.login.LoginData
import cthree.user.flypass.ui.dialog.DialogCaller
import cthree.user.flypass.utils.AlertButton
import cthree.user.flypass.utils.Utils
import cthree.user.flypass.viewmodels.PreferencesViewModel
import cthree.user.flypass.viewmodels.UserViewModel
import cthree.user.flypass.viewmodels.WishlistViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "FlightConfirmationFragment"
@AndroidEntryPoint
class FlightConfirmationFragment : Fragment(), MenuProvider{

    private lateinit var progressAlertDialogBuilder : MaterialAlertDialogBuilder
    private lateinit var progressAlertDialog        : AlertDialog
    private lateinit var binding: FragmentFilghtConfirmationBinding
    private val wishlistVM: WishlistViewModel by viewModels()
    private val prefVM: PreferencesViewModel by viewModels()
    private lateinit var depFlight: Flight
    private var arrFlight: Flight? = null
    private val userVM: UserViewModel by viewModels()
    private lateinit var userToken: String
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        oneTapClient = Identity.getSignInClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilghtConfirmationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initProgressDialog()
        configSignInGoogle()
        setupToolbar()
        getArgs()
        setViews()

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.confirmLayout.btnConfirm.setOnClickListener {
            navigateToBooking()
        }
        wishlistVM.postWishlistResponse().observe(viewLifecycleOwner){
            if(it != null){
                progressAlertDialog.dismiss()
                DialogCaller(requireActivity())
                    .setTitle(R.string.wishlist_add_title)
                    .setMessage(R.string.wishlist_add_subtitle)
                    .setPrimaryButton(R.string.wishlist_add_btn){ dialog, _ ->
                        run{
                            dialog.dismiss()
                        }
                    }
                    .create(layoutInflater, AlertButton.ONE)
                    .show()
            }
        }
        userVM.loginToken().observe(viewLifecycleOwner){
            if(it != null){
                prefVM.saveToken(it)
                userToken = it
                wishlistVM.postWishlist(it, depFlight.id)
                progressAlertDialog.dismiss()
            }
        }
    }

    private fun navigateToBooking() {
        val directions = FlightConfirmationFragmentDirections.actionFlightConfirmationFragmentToBookingFragment(depFlight, arrFlight)
        findNavController().navigate(directions)
    }

    private fun setViews() {
        // change visibility if booking is round trip
        binding.rvArriveTitle.isVisible = arrFlight != null
        binding.roundFlightDesc.root.isVisible= arrFlight != null
        binding.roundFlightDetails.root.isVisible = arrFlight != null

        // set Small Description Flight
        with(binding.flightDesc){
            tvFlightCode.text = depFlight.flightCode
            tvSeatClass.text = "Economy"
            tvFlightDirect.text = "Direct"
            tvArriveCity.text = depFlight.arrivalAirport.city
            tvDepartCity.text = depFlight.departureAirport.city
            tvAirplaneName.text = depFlight.airline.name
            Glide.with(root)
                .load(depFlight.airline.image)
                .into(ivAirplaneLogo)
        }

        // set Details Description Flights
        with(binding.flightDetails){
            tvFlightCode.text = depFlight.flightCode
            tvAirplaneName.text = depFlight.airline.name
            tvArriveCity.text = depFlight.arrivalAirport.city
            tvDepartCity.text = depFlight.departureAirport.city
            tvArrivalDate.text = Utils.convertDateToDayDate(depFlight.arrivalDate)
            tvDepartDate.text = Utils.convertDateToDayDate(depFlight.departureDate)
            tvFlightCode.text = depFlight.flightCode
            tvArriveTime.text = Utils.formattedTime(depFlight.arrivalTime)
            tvDepartTime.text = Utils.formattedTime(depFlight.departureTime)
            tvArrivalAirportName.text = depFlight.arrivalAirport.name
            tvDepartAirportName.text = depFlight.departureAirport.name
            Glide.with(root)
                .load(depFlight.airline.image)
                .into(ivAirplaneLogo)
        }

        // set show Details Flight
        with(binding.flightDetails.cbShowMore){
            setOnClickListener {
                binding.flightShowDetails.root.isVisible = isChecked
            }
        }
        with(binding.flightShowDetails){
            tvFlightType.text = depFlight.airplane.model
            tvBaggage.text = depFlight.baggage.toString()
        }


        if(arrFlight != null){
            // set Small Description Flight
            with(binding.roundFlightDesc){
                tvFlightCode.text = arrFlight!!.flightCode
                tvSeatClass.text = "Economy"
                tvFlightDirect.text = "Direct"
                tvArriveCity.text = arrFlight!!.arrivalAirport.city
                tvDepartCity.text = arrFlight!!.departureAirport.city
                tvAirplaneName.text = arrFlight!!.airline.name
                Glide.with(root)
                    .load(arrFlight!!.airline.image)
                    .into(ivAirplaneLogo)
            }

            // set Details Description Flights
            with(binding.roundFlightDetails){
                tvFlightCode.text = arrFlight!!.flightCode
                tvAirplaneName.text = arrFlight!!.airline.name
                tvArriveCity.text = arrFlight!!.arrivalAirport.city
                tvDepartCity.text = arrFlight!!.departureAirport.city
                tvArrivalDate.text = Utils.convertDateToDayDate(arrFlight!!.arrivalDate)
                tvDepartDate.text = Utils.convertDateToDayDate(arrFlight!!.departureDate)
                tvFlightCode.text = arrFlight!!.flightCode
                tvArriveTime.text = Utils.formattedTime(arrFlight!!.arrivalTime)
                tvDepartTime.text = Utils.formattedTime(arrFlight!!.departureTime)
                tvArrivalAirportName.text = arrFlight!!.arrivalAirport.name
                tvDepartAirportName.text = arrFlight!!.departureAirport.name
                Glide.with(root)
                    .load(arrFlight!!.airline.image)
                    .into(ivAirplaneLogo)
            }

            // set show Details Flight
            with(binding.flightDetails.cbShowMore){
                setOnClickListener {
                    binding.flightShowDetails.root.isVisible = isChecked
                    binding.flightDetails.cbShowMore.text = if(isChecked) "Show Less" else "Show More"
                }
            }
            with(binding.roundFlightDetails.cbShowMore){
                setOnClickListener {
                    binding.roundFlightShowDetails.root.isVisible = isChecked
                    binding.roundFlightDetails.cbShowMore.text = if(isChecked) "Show Less" else "Show More"
                }
            }
            with(binding.roundFlightShowDetails){
                tvFlightType.text = arrFlight!!.airplane.model
                tvBaggage.text = arrFlight!!.baggage.toString()
            }
        }

        val arrPrice = arrFlight?.price ?: 0
        val totalPrice = depFlight.price + arrPrice
        binding.confirmLayout.tvPrice.text = Utils.formattedMoney(totalPrice)

    }



    private fun getArgs(){
        val bundle = arguments
        if(bundle == null){
            Log.e(TAG, "onViewCreated: Args Failed")
            return
        }
        val args = FlightConfirmationFragmentArgs.fromBundle(bundle)
        depFlight = args.depFlight
        arrFlight = args.arrFlight
        Log.d(TAG, "getArgs: Flight ${args.depFlight}")
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

    private fun configSignInGoogle() {
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(requireContext().getString(R.string.web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .setAutoSelectEnabled(true)
            .build()
    }

    private fun initProgressDialog(){
        val progressBarBinding = DialogProgressBarBinding.inflate(layoutInflater, null, false)
        progressAlertDialogBuilder.setView(progressBarBinding.root)

        progressAlertDialog = progressAlertDialogBuilder.create()
        progressAlertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        progressAlertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
    }


    private fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.title = "Flight Confirmation"
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.add_wishlist_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId){
            R.id.addWishlist ->{
                Log.d(TAG, "onMenuItemSelected: Clicked")
                checkUserMember()
                true
            }
            else -> false
        }
    }

    private fun checkUserMember(){
        prefVM.dataUser.observe(viewLifecycleOwner){
            if(it.token.isNotEmpty()){
                if(!Utils.isTokenExpired(it.token.toString())){
                    progressAlertDialog.show()
                    wishlistVM.postWishlist(it.token, depFlight.id)
                }else{
                    DialogCaller(requireActivity())
                        .setTitle(R.string.token_expired_title)
                        .setMessage(R.string.token_expired_subtitle)
                        .setPrimaryButton(R.string.token_expired_login){dialog, _ ->
                            run{
                                // handle data
                                prefVM.clearToken()
                                prefVM.clearRefreshToken()
                                dialog.dismiss()
                                callLoginDialog()
                            }
                        }
                        .setSecondaryButton(R.string.token_expired_later){dialog, _ ->
                            run{
                                dialog.dismiss()
                            }
                        }
                        .create(layoutInflater, AlertButton.TOKEN)
                        .show()
                }
            }else{
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