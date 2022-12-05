package cthree.user.flypass.ui.booking

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cthree.user.flypass.R
import cthree.user.flypass.adapter.BookingBaggageAdapter
import cthree.user.flypass.adapter.TravelerDetailsAdapter
import cthree.user.flypass.data.Baggage
import cthree.user.flypass.data.Contact
import cthree.user.flypass.data.Traveler
import cthree.user.flypass.databinding.FragmentBookingBinding
import cthree.user.flypass.models.flight.Flight
import cthree.user.flypass.utils.SessionManager
import cthree.user.flypass.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "BookingFragment"

@AndroidEntryPoint
class BookingFragment : Fragment() {

    private lateinit var binding: FragmentBookingBinding
    private val contactDetailsFragment: ContactDetailsFragment = ContactDetailsFragment()
    private val travelerDetailsFragment: TravelerDetailsFragment = TravelerDetailsFragment()
    private val baggageFragment: BaggageFragment = BaggageFragment()
    private lateinit var sessionManager: SessionManager
    private val travelerList = arrayListOf<Traveler>()
    private var travelerItemPos = 0
    private var isEdit = false
    private lateinit var depFlight: Flight
    private var arrFlight: Flight? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        getArgs()
        setupToolbar()
        setInputConfig()
        setFlightInfo()

        binding.confirmLayout.btnConfirm.setOnClickListener {
            val directions = BookingFragmentDirections.actionBookingFragmentToPaymentFragment(depFlight, arrFlight)
            findNavController().navigate(directions)
        }

    }

    private fun setFlightInfo() {
        binding.selectedArriveFlight.root.isVisible = arrFlight != null

        // set Flight Info
        with(binding.selectedDepartFlight){
            tvDate.text = Utils.formattedDateOnly(depFlight.departureDate)
            tvPaxCount.text = "1"
            tvFlightDirect.text = "Direct"
            tvArriveCity.text = depFlight.arrivalAirport.city
            tvDepartCity.text = depFlight.departureAirport.city
            iataDepartAirport.text = depFlight.departureAirport.iata
            iataArriveAirport.text= depFlight.arrivalAirport.iata
        }

        if(arrFlight != null){
            with(binding.selectedArriveFlight){
                tvDate.text = Utils.formattedDateOnly(arrFlight!!.departureDate)
                tvDepArr.text = "Arrive"
                tvPaxCount.text = "1"
                tvFlightDirect.text = "Direct"
                tvArriveCity.text = arrFlight!!.arrivalAirport.city
                tvDepartCity.text = arrFlight!!.departureAirport.city
                iataDepartAirport.text = arrFlight!!.departureAirport.iata
                iataArriveAirport.text= arrFlight!!.arrivalAirport.iata
            }
        }

        val arrPrice = arrFlight?.price ?: 0
        val totalPrice = depFlight.price + arrPrice
        binding.confirmLayout.tvPrice.text = Utils.formattedMoney(totalPrice)
    }

    private fun setInputConfig(){
        binding.tvEdit.setOnClickListener {
            contactDetailsFragment.show(requireActivity().supportFragmentManager, contactDetailsFragment.tag)
        }
        contactDetailsFragment.setOnClickListener(object : ContactDetailsFragment.OnClickListener{
            @SuppressLint("SetTextI18n")
            override fun onClick(contact: Contact) {
                binding.tvEmail.text = contact.email
                binding.tvFullName.text = "${contact.name}, ${contact.surname}"
                binding.tvPhoneNumber.text = contact.phoneNumber
            }
        })

        val travelerAdapter = TravelerDetailsAdapter(sessionManager.getPassenger())
        binding.rvTravelersDetail.adapter = travelerAdapter
        travelerAdapter.submitList(travelerList)
        binding.rvTravelersDetail.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
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
            override fun onClick(traveler: Traveler) {
                if(!isEdit)
                    travelerList.add(traveler)
                else
                    travelerList[travelerItemPos] = traveler
                travelerAdapter.modifyItemList(travelerList, travelerItemPos)
            }
        })

        val bookingBaggageAdapter = BookingBaggageAdapter()
        binding.rvPassengerBaggage.adapter = bookingBaggageAdapter
        binding.rvPassengerBaggage.layoutManager = LinearLayoutManager(requireContext())
        binding.btnAddBaggage.setOnClickListener {
            baggageFragment.show(requireActivity().supportFragmentManager, baggageFragment.tag)
        }
        baggageFragment.setOnClickListener(object : BaggageFragment.OnClickListener{
            override fun onClick(baggageList: List<Baggage>) {

            }
        })
    }

    private fun getArgs(){
        val bundle = arguments
        if(bundle == null){
            Log.e(TAG, "onViewCreated: Args Failed")
            return
        }
        val args = BookingFragmentArgs.fromBundle(bundle)
        depFlight = args.depFlight
        arrFlight = args.arrFlight
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



}