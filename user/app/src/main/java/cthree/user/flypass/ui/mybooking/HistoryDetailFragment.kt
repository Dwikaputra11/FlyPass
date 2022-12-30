package cthree.user.flypass.ui.mybooking

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import cthree.user.flypass.R
import cthree.user.flypass.adapter.TravelerDetailsAdapter
import cthree.user.flypass.data.Traveler
import cthree.user.flypass.databinding.FragmentHistoryDetailBinding
import cthree.user.flypass.models.booking.bookings.Booking
import cthree.user.flypass.utils.BookingStatus
import cthree.user.flypass.utils.Utils
import cthree.user.flypass.utils.Utils.convertDateToDayDate
import cthree.user.flypass.utils.Utils.formattedMoney
import cthree.user.flypass.utils.Utils.formattedTime
import cthree.user.flypass.utils.Utils.passengerToTravelerDataClass

private const val TAG = "HistoryDetailFragment"

class HistoryDetailFragment : Fragment() {

    private lateinit var binding: FragmentHistoryDetailBinding
    private lateinit var booking: Booking

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getArgs()
        setViews()
        setupToolbar()
    }

    private fun setViews() {

        when (booking.bookingStatus.id) {
            BookingStatus.PAID.ordinal -> {
                binding.cvBookingStatus.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.orange_500))
            }
            BookingStatus.WAITING.ordinal -> {
                binding.cvBookingStatus.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color_primary))
            }
            BookingStatus.COMPLETE.ordinal -> {
                binding.cvBookingStatus.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green_500))
            }
        }
        binding.tvStatus.text = booking.bookingStatus.name

        val adapter                         = TravelerDetailsAdapter(booking.passengerQty)
        binding.rvPassenger.adapter         = adapter
        binding.rvPassenger.layoutManager   = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter.submitList(passengerToTravelerDataClass(booking.passengers))
        adapter.setOnItemClickListener(object : TravelerDetailsAdapter.OnItemClickListener{
            override fun onItemClick(traveler: Traveler?, position: Int) {
                Log.d(TAG, "onItemClick: $traveler")
            }
        })

        with(binding.flightDesc){
            tvFlightCode.text = booking.depFlightBooking.flightCode
            tvSeatClass.text = booking.depFlightBooking.flightClass.name
            tvFlightDirect.text = "Direct"
            tvArriveCity.text = booking.depFlightBooking.arrivalAirport.city
            tvDepartCity.text = booking.depFlightBooking.departureAirport.city
            tvAirplaneName.text = booking.depFlightBooking.airline.name
            Glide.with(root)
                .load(booking.depFlightBooking.airline.image)
                .into(ivAirplaneLogo)
        }

        binding.roundFlightDetails.root.isVisible          = booking.roundtrip
        binding.contactDetails.tvContactEmail.text         = booking.passengerContact.email
        binding.contactDetails.tvContactNumber.text        = booking.passengerContact.phone
        binding.contactDetails.tvContactTitle.text         = booking.passengerContact.title
        binding.contactDetails.tvContactFirstName.text     = booking.passengerContact.firstName
        binding.contactDetails.tvContactLastName.text      = booking.passengerContact.lastName
        with(binding.paymentSummary){
            tvPrice.text = formattedMoney(booking.totalPrice)
            tvAddsOn.text = formattedMoney(booking.totalPassengerBaggagePrice)
            tvTotalPrice.text = formattedMoney(booking.totalPrice)
        }


        // set Details Description Flights
        with(binding.flightDetails){
            tvFlightCode.text           = booking.depFlightBooking.flightCode
            tvAirplaneName.text         = booking.depFlightBooking.airline.name
            tvArriveCity.text           = booking.depFlightBooking.arrivalAirport.city
            tvDepartCity.text           = booking.depFlightBooking.departureAirport.city
            tvArrivalDate.text          = convertDateToDayDate(booking.depFlightBooking.arrivalDate)
            tvDepartDate.text           = convertDateToDayDate(booking.depFlightBooking.departureDate)
            tvArriveTime.text           = formattedTime(booking.depFlightBooking.arrivalTime)
            tvDepartTime.text           = formattedTime(booking.depFlightBooking.departureTime)
            tvArrivalAirportName.text   = booking.depFlightBooking.arrivalAirport.name
            tvDepartAirportName.text    = booking.depFlightBooking.departureAirport.name
            Glide.with(root)
                .load(booking.depFlightBooking.airline.image)
                .into(ivAirplaneLogo)
        }

        // set show Details Flight
        with(binding.flightDetails.cbShowMore){
            setOnClickListener {
                binding.flightShowDetails.root.isVisible = isChecked
            }
        }
        with(binding.flightShowDetails){
            tvFlightType.text   = booking.depFlightBooking.airplane.model
            tvBaggage.text      = booking.depFlightBooking.baggage.toString()
        }


        if(booking.arrFlightBooking != null){
            val arrFlight = booking.arrFlightBooking!!
            // set Details Description Flights
            with(binding.roundFlightDetails){
                tvFlightCode.text           = arrFlight.flightCode
                tvAirplaneName.text         = arrFlight.airline.name
                tvArriveCity.text           = arrFlight.arrivalAirport.city
                tvDepartCity.text           = arrFlight.departureAirport.city
                tvArrivalDate.text          = convertDateToDayDate(arrFlight.arrivalDate)
                tvDepartDate.text           = convertDateToDayDate(arrFlight.departureDate)
                tvArriveTime.text           = formattedTime(arrFlight.arrivalTime)
                tvDepartTime.text           = formattedTime(arrFlight.departureTime)
                tvArrivalAirportName.text   = arrFlight.arrivalAirport.name
                tvDepartAirportName.text    = arrFlight.departureAirport.name
                Glide.with(root)
                    .load(arrFlight.airline.image)
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
                tvFlightType.text   = arrFlight.airplane.model
                tvBaggage.text      = arrFlight.baggage.toString()
            }
        }
    }

    private fun getArgs(){
        val bundle = arguments ?: return
        val args = HistoryDetailFragmentArgs.fromBundle(bundle)
        booking = args.booking
    }

    private fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.title = "Booking Details"
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }
    }
}