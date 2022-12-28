package cthree.user.flypass.ui.booking

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import cthree.user.flypass.R
import cthree.user.flypass.adapter.BookingBaggageAdapter
import cthree.user.flypass.adapter.TravelerDetailsAdapter
import cthree.user.flypass.data.Contact
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
    private val prefViewModel                       : PreferencesViewModel by viewModels()
    private val userVM                              : UserViewModel by viewModels()
    private lateinit var contactData                : Contact
    private val travelerList        = arrayListOf<Traveler>()
    private var passengerList       = mutableListOf<Passenger>()
    private val baggagePassList     = mutableListOf<PassengerBaggage>()
    private var travelerItemPos     = 0
    private var passengerAmount     = 0
    private var isEdit              = false
    private var arrFlight: Flight?  = null
    private var isExpired           = false
    private var userToken: String?  = null
    private var token: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwibmFtZSI6IkpvaG4gRG9lIiwiaW1hZ2UiOiJodHRwczovL3Jlcy5jbG91ZGluYXJ5LmNvbS9kaXhscnVsZW4vaW1hZ2UvdXBsb2FkL3YxNjY5NjkwMjQ2L3Byb2ZpbGUveDBzdnZ0ZnViamc3dWxwZzBpbTMuanBnIiwiZW1haWwiOiJqb2huZG9lQG1haWwuY29tIiwiYmlydGhEYXRlIjpudWxsLCJnZW5kZXIiOm51bGwsInBob25lIjpudWxsLCJyb2xlSWQiOjEsImNyZWF0ZWRBdCI6IjIwMjItMTItMDFUMTA6MjQ6NTEuNDYwWiIsInVwZGF0ZWRBdCI6IjIwMjItMTItMTFUMjA6NDY6NDkuOTE1WiIsImlhdCI6MTY3MDc5NTM1MSwiZXhwIjoxNjcwODE2OTUxfQ.YZoTufde4f6DMHpWi6QwTC0Q5oPPGMUTuBOYykJ4ED4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        sessionManager = SessionManager(requireActivity())
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

        userVM.getRefreshToken().observe(viewLifecycleOwner){
            if(it != null){
                prefViewModel.saveRefreshToken(it)
            }
        }

        userVM.loginToken().observe(viewLifecycleOwner){
            if(it != null){
                progressAlertDialog.dismiss()
                sessionManager.setToken(it)
                // save data profile to proto
                val profile = Utils.decodeAccountToken(it)
                userToken = it
                prefViewModel.saveToken(it)
                prefViewModel.saveData(profile)
                sessionManager.setUserId(profile.id)
            }
        }

        userVM.getAccessToken().observe(viewLifecycleOwner){
            // get new access token from refresh token
            if(it != null){
                prefViewModel.saveToken(it.accessToken)
                userToken = it.accessToken
            }
        }

        prefViewModel.dataUser.observe(viewLifecycleOwner){
            if(it.token.isNotEmpty()){
                if(Utils.isTokenExpired(it.token)){
                    userToken = null
                }else if(!Utils.isTokenExpired(it.token)){
                    Log.d(TAG, "From Access Token")
                    userToken = it.token
                }else{
                    Log.d(TAG, "From Refresh Token")
                    userVM.refreshToken(it.refreshToken)
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
                    flyPassCode = it.booking.bookingCode,
                    contactData = contactData,
                    passengerList = travelerList.toTypedArray(),
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
                    DialogCaller(requireActivity())
                        .setTitle(R.string.token_expired_title)
                        .setMessage(R.string.token_expired_subtitle)
                        .setPrimaryButton(R.string.token_expired_login){dialog, _ ->
                            run{
                                // handle data
                                prefViewModel.clearToken()
                                prefViewModel.clearRefreshToken()
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
                    Log.d(TAG, "onClick Google: $email, $password")
                    dialog.dismiss()
                }
            })
            .create(layoutInflater, AlertButton.LOGIN)
            .show()
    }

    private fun saveDataToPref() {
        val gson = Gson()
        val depFlightJson = gson.toJson(depFlight)
        val arrFlightJson = if(arrFlight != null) gson.toJson(arrFlight) else null
        val passengerListJson = gson.toJson(passengerList)
        val contactJson = gson.toJson(contactData)
        val baggageListJson = gson.toJson(baggagePassList)
        Log.d(TAG, "saveDataToPref: $depFlightJson")
        Log.d(TAG, "saveDataToPref: $arrFlightJson")
        Log.d(TAG, "saveDataToPref: $passengerListJson")
        Log.d(TAG, "saveDataToPref: $contactJson")
        prefViewModel.saveBookingData(depFlightJson, arrFlightJson, passengerListJson, contactJson, baggageListJson)
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