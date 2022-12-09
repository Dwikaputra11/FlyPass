package cthree.user.flypass.ui.booking

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.user.flypass.R
import cthree.user.flypass.adapter.BookingBaggageAdapter
import cthree.user.flypass.adapter.TravelerDetailsAdapter
import cthree.user.flypass.data.Baggage
import cthree.user.flypass.data.Contact
import cthree.user.flypass.data.Traveler
import cthree.user.flypass.databinding.DialogProgressBarBinding
import cthree.user.flypass.databinding.FragmentBookingBinding
import cthree.user.flypass.models.booking.request.BookingRequest
import cthree.user.flypass.models.booking.request.Passenger
import cthree.user.flypass.models.flight.Flight
import cthree.user.flypass.utils.SessionManager
import cthree.user.flypass.utils.Utils
import cthree.user.flypass.viewmodels.BookingViewModel
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
    private val userViewModel                       : UserViewModel by viewModels()
    private lateinit var contactData                : Contact
    private val travelerList        = arrayListOf<Traveler>()
    private val passengerList       = arrayListOf<Passenger>()
    private var travelerItemPos     = 0
    private var passengerAmount     = 0
    private var isEdit              = false
    private var arrFlight: Flight?  = null
    private var isExpired           = false
    private var userToken: String?  = null

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

        userViewModel.dataUser.observe(viewLifecycleOwner){
            if(it.token != null){
                isExpired = Utils.isTokenExpired(it.token)
                userToken = it.token
            }
        }

        bookingViewModel.getBookingResp().observe(viewLifecycleOwner){
            if(it != null){
                Log.d(TAG, "onViewCreated: Booking Success")
                val directions = BookingFragmentDirections.actionBookingFragmentToPaymentFragment(
                    depFlight = depFlight,
                    arrFlight = arrFlight,
                    flyPassCode = it.bookingDetail.bookingCode,
                    contactData = contactData,
                    passengerList = travelerList.toTypedArray()
                )
                findNavController().navigate(directions)
            }
        }

        binding.confirmLayout.btnConfirm.setOnClickListener {
            if(isValid()){
                if(!isExpired){
                    val arrFlightId = if(arrFlight != null) arrFlight!!.id.toString() else null
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
                    bookingViewModel.postBookingRequest(userToken,booking)
                }else{
                    // handle token expired
                }
            }

        }

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
        bookingBaggageAdapter.submitList(emptyList())
        baggageFragment.setOnClickListener(object : BaggageFragment.OnClickListener{
            override fun onClick(baggageList: List<Baggage?>) {
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