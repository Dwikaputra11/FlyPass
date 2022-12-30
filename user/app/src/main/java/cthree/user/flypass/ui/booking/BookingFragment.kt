package cthree.user.flypass.ui.booking

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import cthree.user.flypass.R
import cthree.user.flypass.adapter.BookingBaggageAdapter
import cthree.user.flypass.adapter.TravelerDetailsAdapter
import cthree.user.flypass.data.Contact
import cthree.user.flypass.data.GoogleTokenRequest
import cthree.user.flypass.data.PassengerBaggage
import cthree.user.flypass.data.Traveler
import cthree.user.flypass.databinding.DialogProgressBarBinding
import cthree.user.flypass.databinding.FragmentBookingBinding
import cthree.user.flypass.models.booking.request.BookingRequest
import cthree.user.flypass.models.booking.request.Passenger
import cthree.user.flypass.models.flight.Flight
import cthree.user.flypass.models.login.LoginData
import cthree.user.flypass.ui.dialog.DialogCaller
import cthree.user.flypass.utils.AlertButton
import cthree.user.flypass.utils.SessionManager
import cthree.user.flypass.utils.Utils
import cthree.user.flypass.viewmodels.BookingViewModel
import cthree.user.flypass.viewmodels.PreferencesViewModel
import cthree.user.flypass.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "BookingFragment"

@AndroidEntryPoint
class BookingFragment : Fragment() {

    private lateinit var binding                    : FragmentBookingBinding
    private val contactDetailsFragment              : ContactDetailsFragment = ContactDetailsFragment()
    private val travelerDetailsFragment             : TravelerDetailsFragment = TravelerDetailsFragment()
    private val baggageFragment                     : BaggageFragment = BaggageFragment()
    private lateinit var sessionManager             : SessionManager
    private lateinit var depFlight                  : Flight
    private lateinit var progressAlertDialogBuilder : MaterialAlertDialogBuilder
    private lateinit var progressAlertDialog        : AlertDialog
    private val bookingViewModel                    : BookingViewModel by viewModels()
    private val prefVM                              : PreferencesViewModel by viewModels()
    private val userVM                              : UserViewModel by viewModels()
    private lateinit var contactData                : Contact
    private val travelerList        = arrayListOf<Traveler>()
    private var passengerList       = mutableListOf<Passenger>()
    private val baggagePassList     = mutableListOf<PassengerBaggage>()
    private var travelerItemPos     = 0
    private var passengerAmount     = 0
    private var isEdit              = false
    private var arrFlight: Flight?  = null
    private var userToken: String?  = null
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
        sessionManager = SessionManager(requireActivity())
        oneTapClient = Identity.getSignInClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        passengerAmount = sessionManager.getPassenger()
        getArgs()
        setupToolbar()
        setInputConfig()
        setFlightInfo()
        initProgressDialog()
        configSignInGoogle()
        initTravelerBaggage()

        userVM.loginToken().observe(viewLifecycleOwner){
            if(it != null){
                progressAlertDialog.dismiss()
                sessionManager.setToken(it)
                val profile = Utils.decodeAccountToken(it)
                // save incoming data
                prefVM.saveToken(it)
                userToken = it
                userVM.saveToken(it)
                prefVM.saveData(profile)
            }
        }

        userVM.getAccessToken().observe(viewLifecycleOwner){
            // get new access token from refresh token
            if(it != null){
                prefVM.saveToken(it.accessToken)
                userToken = it.accessToken
            }
        }

        prefVM.dataUser.observe(viewLifecycleOwner){
            if(it.token.isNotEmpty()){
                if(Utils.isTokenExpired(it.token)){
                    userToken = null
                }else if(!Utils.isTokenExpired(it.token)){
                    Log.d(TAG, "From Access Token")
                    userToken = it.token
                }
                Log.d(TAG, "User Token: $userToken")
            }
        }

        // test token expired
//        userToken = if(Utils.isTokenExpired(token)) null else ""

        bookingViewModel.bookingResp.observe(viewLifecycleOwner){
            if(it != null){
                Log.d(TAG, "onViewCreated: Booking Success")
                val directions = BookingFragmentDirections.actionBookingFragmentToPaymentFragment(
                    depFlight = depFlight,
                    arrFlight = arrFlight,
                    flypassCode = it.booking.bookingCode,
                    travelerList = travelerList.toTypedArray(),
                    booking = it,
                    bookingId = it.booking.id
                )
                findNavController().navigate(directions)
                // to make booking resp not navigate to payment again when payment click back button to pop backstack
                bookingViewModel.bookingResp.postValue(null)
            }
        }

        binding.confirmLayout.btnConfirm.setOnClickListener {
            Log.d(TAG, "Button clicked: ")
            if(isValid()){
                val arrFlightId = if(arrFlight != null) arrFlight!!.id.toString() else null
                for(i in 0 until passengerAmount){
                    passengerList[i].baggage = baggagePassList[i].baggageList
                }
                val booking = BookingRequest(
                    contactEmail = binding.tvEmail.text.toString(),
                    contactFirstName = contactData.firstName,
                    contactLastName = contactData.lastName,
                    contactPhone = contactData.phoneNumber,
                    contactTitle = contactData.title,
                    flight1Id = depFlight.id.toString(),
                    flight2Id = arrFlightId,
                    passenger = passengerList,
                )
                if(userToken == null){
                    showSessionExpiredDialog(booking)
                }else{
                    bookingViewModel.postBookingRequest(userToken, booking)
                }
            }else{
                DialogCaller(requireActivity())
                    .setTitle(R.string.booking_empty_field_title)
                    .setMessage(R.string.booking_empty_field_subtitle)
                    .setPrimaryButton(R.string.confirm_one_btn_dialog) { dialog, _ ->
                        run {
                            dialog.dismiss()
                        }
                    }
                    .create(layoutInflater, AlertButton.ONE)
                    .show()
            }

        }

    }

    private fun initTravelerBaggage() {
        for(i in 0 until passengerAmount){
            if(arrFlight != null)
                baggagePassList.add(PassengerBaggage(mutableListOf("20", "20")))
            else
                baggagePassList.add(PassengerBaggage(mutableListOf("20")))
        }
    }

    private fun showSessionExpiredDialog(booking: BookingRequest){
        DialogCaller(requireActivity())
            .setTitle(R.string.token_expired_title)
            .setMessage(R.string.token_expired_subtitle)
            .setPrimaryButton(R.string.token_expired_login){dialog, _ ->
                run{
                    // handle data
                    dialog.dismiss()
                    callLoginDialog()
                }
            }
            .setSecondaryButton(R.string.token_expired_later){dialog, _ ->
                run{
                    // post the booking without token
                    bookingViewModel.postBookingRequest(null,booking)
                    dialog.dismiss()
                }
            }
            .create(layoutInflater, AlertButton.TOKEN)
            .show()
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
                    dialog.dismiss()
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

    private fun isValid(): Boolean {
        if(binding.tvPhoneNumber.text.isNotEmpty()
            && binding.tvEmail.text.isNotEmpty()
            && binding.tvFullName.text.isNotEmpty()
            && passengerList.size == passengerAmount
        ) return true

        return false
    }

    private fun setFlightInfo() {
        binding.selectedArriveFlight.root.isVisible = arrFlight != null

        // set Flight Info
        with(binding.selectedDepartFlight){
            tvDate.text                 = Utils.formattedDateOnly(depFlight.departureDate)
            tvPaxCount.text             = "1"
            tvFlightDirect.text         = "Direct"
            tvArriveCity.text           = depFlight.arrivalAirport.city
            tvDepartCity.text           = depFlight.departureAirport.city
            iataDepartAirport.text      = depFlight.departureAirport.iata
            iataArriveAirport.text      = depFlight.arrivalAirport.iata
        }

        if(arrFlight != null){
            with(binding.selectedArriveFlight){
                tvDate.text             = Utils.formattedDateOnly(arrFlight!!.departureDate)
                tvDepArr.text           = "Arrive"
                tvPaxCount.text         = "1"
                tvFlightDirect.text     = "Direct"
                tvArriveCity.text       = arrFlight!!.arrivalAirport.city
                tvDepartCity.text       = arrFlight!!.departureAirport.city
                iataDepartAirport.text  = arrFlight!!.departureAirport.iata
                iataArriveAirport.text  = arrFlight!!.arrivalAirport.iata
            }
        }

        val arrPrice                        = arrFlight?.price ?: 0
        val totalPrice                      = depFlight.price + arrPrice
        binding.confirmLayout.tvPrice.text  = Utils.formattedMoney(totalPrice)
    }

    private fun setInputConfig(){
        binding.tvEdit.setOnClickListener {
            contactDetailsFragment.show(requireActivity().supportFragmentManager, contactDetailsFragment.tag)
        }
        contactDetailsFragment.setOnClickListener(object : ContactDetailsFragment.OnClickListener{
            @SuppressLint("SetTextI18n")
            override fun onClick(contact: Contact) {
                contactData = contact
                binding.tvEmail.text        = contact.email
                binding.tvFullName.text     = "${contact.firstName}, ${contact.lastName}"
                binding.tvPhoneNumber.text  = contact.phoneNumber
            }
        })

        val travelerAdapter                         = TravelerDetailsAdapter(passengerAmount)
        binding.rvTravelersDetail.adapter           = travelerAdapter
        binding.rvTravelersDetail.layoutManager     = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        travelerAdapter.submitList(travelerList)
        travelerAdapter.setOnItemClickListener(object : TravelerDetailsAdapter.OnItemClickListener{
            override fun onItemClick(traveler: Traveler?,position: Int) {
                isEdit = false
                travelerItemPos = position
                if(traveler != null) {
                    isEdit = true
                    travelerDetailsFragment.setTraveler(traveler)
                }
                travelerDetailsFragment.show(requireActivity().supportFragmentManager, travelerDetailsFragment.tag)
            }
        })

        travelerDetailsFragment.setOnClickListener(object : TravelerDetailsFragment.OnClickListener{
            override fun onClick(traveler: Traveler, passenger: Passenger) {
                if(!isEdit){
                    travelerList.add(traveler)
                    passengerList.add(passenger)
                }
                else{
                    travelerList[travelerItemPos] = traveler
                    passengerList[travelerItemPos] = passenger
                }
                travelerAdapter.modifyItemList(travelerList, travelerItemPos)
            }
        })

        val bookingBaggageAdapter                   = BookingBaggageAdapter(passengerAmount)
        binding.rvPassengerBaggage.adapter          = bookingBaggageAdapter
        binding.rvPassengerBaggage.layoutManager    = LinearLayoutManager(requireContext())
        binding.btnAddBaggage.setOnClickListener {
            baggageFragment.show(requireActivity().supportFragmentManager, baggageFragment.tag)
        }
        baggagePassList.addAll(bookingBaggageAdapter.getBaggageDefaultVal())
        bookingBaggageAdapter.submitList(emptyList())
        baggageFragment.setOnClickListener(object : BaggageFragment.OnClickListener{
            override fun onClick(baggageList: List<PassengerBaggage>) {
                baggagePassList.addAll(baggageList)
                bookingBaggageAdapter.submitList(baggageList)
            }
        })
    }

    private fun getArgs(){
        val bundle = arguments
        if(bundle == null){
            Log.e(TAG, "onViewCreated: Args Failed")
            return
        }
        val args    = BookingFragmentArgs.fromBundle(bundle)
        depFlight   = args.depFlight
        arrFlight   = args.arrFlight
        Log.d(TAG, "getArgs: Flight ${args.depFlight}")
    }

    private fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.title = "Booking"
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }
    }

    private fun initProgressDialog(){
        val progressBarBinding = DialogProgressBarBinding.inflate(layoutInflater, null, false)
        progressAlertDialogBuilder.setView(progressBarBinding.root)

        progressAlertDialog = progressAlertDialogBuilder.create()
        progressAlertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        progressAlertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
    }



}