package cthree.user.flypass.ui.booking

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import cthree.user.flypass.R
import cthree.user.flypass.adapter.TravelerDetailsAdapter
import cthree.user.flypass.data.Contact
import cthree.user.flypass.data.InputPinMember
import cthree.user.flypass.data.Traveler
import cthree.user.flypass.databinding.FragmentPaymentBinding
import cthree.user.flypass.models.booking.response.BookingResponse
import cthree.user.flypass.models.flight.Flight
import cthree.user.flypass.ui.dialog.DialogCaller
import cthree.user.flypass.ui.flightpay.PinInputFragment
import cthree.user.flypass.utils.AlertButton
import cthree.user.flypass.utils.InputPinFrom
import cthree.user.flypass.utils.SessionManager
import cthree.user.flypass.utils.Utils
import cthree.user.flypass.viewmodels.BookingViewModel
import cthree.user.flypass.viewmodels.FlightPayViewModel
import cthree.user.flypass.viewmodels.PreferencesViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "PaymentFragment"

@AndroidEntryPoint
class PaymentFragment : Fragment() {

    private lateinit var binding                : FragmentPaymentBinding
    private lateinit var depFlight              : Flight
    private var arrFlight                       : Flight? = null
    private lateinit var bookingCode            : String
    private lateinit var paymentMethodFragment  : PaymentMethodFragment
    private lateinit var passengerList          : List<Traveler>
    private var contactData            : Contact? = null
    private var bookingId                       : Int = -1
    private lateinit var sessionManager         : SessionManager
    private var booking                : BookingResponse? = null
    private var totalPrice: Int = 0
    private val flightPayVM: FlightPayViewModel by viewModels()
    private val prefVM: PreferencesViewModel by viewModels()
    private lateinit var userToken: String
    private lateinit var pinMember: String

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        paymentMethodFragment   = PaymentMethodFragment()
        sessionManager          = SessionManager(requireContext())
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getArgs()
        setupToolbar()
        setViews()
        setBottomNav()
        flightPayVM.getUserWallet().observe(viewLifecycleOwner){
            if(it != null){
                if(it.wallet.balance < totalPrice){
                    topUpBalanceDialog()
                }else{
                    requestPinMember()
                }
            }
        }
        prefVM.dataUser.observe(viewLifecycleOwner){
            if(it.token.isNotEmpty()) userToken = it.token
            if(it.pinMember.isNotEmpty()) pinMember = it.pinMember
        }
        binding.btnPayment.setOnClickListener {
            if(binding.paymentDetails.tvPaymentMethod.text == "FlightPay"){
                flightPayVM.userWallet(userToken) // check user wallet balance
            }else{
                val directions = PaymentFragmentDirections.actionPaymentFragmentToTransferBankConfirmFragment(bookingId)
                findNavController().navigate(directions)
            }
        }
    }

    private fun requestPinMember() {
        val pinInputFragment = PinInputFragment(InputPinFrom.PAYMENT)
        pinInputFragment.show(requireActivity().supportFragmentManager.beginTransaction(), pinInputFragment.tag)
        pinInputFragment.setBookingId(bookingId)
    }

    private fun setViews() {
        val adapter                         = TravelerDetailsAdapter(sessionManager.getPassenger())
        binding.rvPassenger.adapter         = adapter
        binding.rvPassenger.layoutManager   = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter.submitList(passengerList)
        adapter.setOnItemClickListener(object : TravelerDetailsAdapter.OnItemClickListener{
            override fun onItemClick(traveler: Traveler?, position: Int) {
                Log.d(TAG, "onItemClick: $traveler")
            }
        })
        if(booking != null){
            // change visibility if booking is round trip
            binding.roundFlightDetails.root.isVisible          = booking!!.booking.roundtrip
            binding.tvFlypassCode.text                         = bookingCode
            binding.contactDetails.tvContactEmail.text         = booking!!.passengerContact.email
            binding.contactDetails.tvContactNumber.text        = booking!!.passengerContact.phone
            binding.contactDetails.tvContactTitle.text         = booking!!.passengerContact.title
            binding.contactDetails.tvContactFirstName.text     = booking!!.passengerContact.firstName
            binding.contactDetails.tvContactLastName.text      = booking!!.passengerContact.lastName
            with(binding.paymentDetails){
                totalPrice = booking!!.booking.totalPrice
                tvPrice.text = Utils.formattedMoney(booking!!.booking.totalPrice)
                tvAddsOn.text = Utils.formattedMoney(booking!!.booking.totalPassengerBaggagePrice)
                tvTotalPrice.text = Utils.formattedMoney(booking!!.booking.totalPrice)
            }
        }else{
            binding.roundFlightDetails.root.isVisible          = arrFlight != null
            binding.tvFlypassCode.text                         = bookingCode
            binding.contactDetails.tvContactEmail.text         = contactData!!.email
            binding.contactDetails.tvContactNumber.text        = contactData!!.phoneNumber
            binding.contactDetails.tvContactTitle.text         = contactData!!.title
            binding.contactDetails.tvContactFirstName.text     = contactData!!.firstName
            binding.contactDetails.tvContactLastName.text      = contactData!!.lastName
            with(binding.paymentDetails){
                totalPrice = depFlight.price + (arrFlight?.price ?: 0)
                tvPrice.text = Utils.formattedMoney(totalPrice)
                tvTotalPrice.text = Utils.formattedMoney(totalPrice)
            }
        }


        // set Details Description Flights
        with(binding.flightDetails){
            tvFlightCode.text           = depFlight.flightCode
            tvAirplaneName.text         = depFlight.airline.name
            tvArriveCity.text           = depFlight.arrivalAirport.city
            tvDepartCity.text           = depFlight.departureAirport.city
            tvArrivalDate.text          = Utils.convertDateToDayDate(depFlight.arrivalDate)
            tvDepartDate.text           = Utils.convertDateToDayDate(depFlight.departureDate)
            tvArriveTime.text           = Utils.formattedTime(depFlight.arrivalTime)
            tvDepartTime.text           = Utils.formattedTime(depFlight.departureTime)
            tvArrivalAirportName.text   = depFlight.arrivalAirport.name
            tvDepartAirportName.text    = depFlight.departureAirport.name
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
            tvFlightType.text   = depFlight.airplane.model
            tvBaggage.text      = depFlight.baggage.toString()
        }


        if(arrFlight != null){

            // set Details Description Flights
            with(binding.roundFlightDetails){
                tvFlightCode.text           = arrFlight!!.flightCode
                tvAirplaneName.text         = arrFlight!!.airline.name
                tvArriveCity.text           = arrFlight!!.arrivalAirport.city
                tvDepartCity.text           = arrFlight!!.departureAirport.city
                tvArrivalDate.text          = Utils.convertDateToDayDate(arrFlight!!.arrivalDate)
                tvDepartDate.text           = Utils.convertDateToDayDate(arrFlight!!.departureDate)
                tvArriveTime.text           = Utils.formattedTime(arrFlight!!.arrivalTime)
                tvDepartTime.text           = Utils.formattedTime(arrFlight!!.departureTime)
                tvArrivalAirportName.text   = arrFlight!!.arrivalAirport.name
                tvDepartAirportName.text    = arrFlight!!.departureAirport.name
                Glide.with(root)
                    .load(arrFlight!!.airline.image)
                    .into(ivAirplaneLogo)
            }

            // set show Details Flight
            with(binding.flightDetails.cbShowMore){
                setOnClickListener {
                    val text                                    = if(isChecked) "Show Less" else "Show More"
                    binding.flightShowDetails.root.isVisible    = isChecked
                    binding.flightDetails.cbShowMore.text       = text
                }
            }
            with(binding.roundFlightDetails.cbShowMore){
                setOnClickListener {
                    val text                                        = if(isChecked) "Show Less" else "Show More"
                    binding.roundFlightShowDetails.root.isVisible   = isChecked
                    binding.roundFlightDetails.cbShowMore.text      = text
                }
            }
            with(binding.roundFlightShowDetails){
                tvFlightType.text   = arrFlight!!.airplane.model
                tvBaggage.text      = arrFlight!!.baggage.toString()
            }
        }

        binding.paymentDetails.llPaymentMethod.setOnClickListener {
            paymentMethodFragment.show(requireActivity().supportFragmentManager, paymentMethodFragment.tag)
        }
        paymentMethodFragment.setOnClickListener(object : PaymentMethodFragment.OnClickListener{
            override fun onClick(method: String) {
                binding.paymentDetails.tvPaymentMethod.text = method
            }
        })
    }

    private fun topUpBalanceDialog(){
        DialogCaller(requireActivity())
            .setTitle(R.string.not_enough_balance_title)
            .setMessage(R.string.not_enough_balance_msg)
            .setPrimaryButton(R.string.top_up_balance_btn){ dialog, _ ->
                run{
                    prefVM.saveActiveBookProcess(bookingCode)
                    findNavController().navigate(R.id.action_paymentFragment_to_topUpFragment)
                    dialog.dismiss()
                }
            }
            .setSecondaryButton(R.string.maybe_later){ dialog, _ ->
                run{
                    dialog.dismiss()
                }
            }.create(layoutInflater, AlertButton.TWO)
            .show()
    }

    private fun getArgs() {
        val bundle = arguments
        if(bundle == null){
            Log.e(TAG, "onViewCreated: Args Failed")
            return
        }
        val args = PaymentFragmentArgs.fromBundle(bundle)
        booking = args.booking
        depFlight = args.depFlight
        arrFlight = args.arrFlight
        passengerList = args.travelerList.toList()
        bookingId = args.bookingId
        bookingCode = args.flypassCode
        contactData = args.contactData
//        Log.d(TAG, "getArgs: Flight ${args.depFlight} Code $bookingCode")
    }

    private fun setBottomNav(){
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.isVisible = false
    }

    // TODO BUAT TRAVELER

    private fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.title = "Payment"
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            Log.d(TAG, "setupToolbar: Clicked")
            Navigation.findNavController(binding.root).popBackStack()
        }
    }
}