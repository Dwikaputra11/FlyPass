package cthree.user.flypass.ui.mybooking

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import cthree.user.flypass.R
import cthree.user.flypass.databinding.FragmentHistorySummaryBinding
import cthree.user.flypass.models.booking.bookings.Booking
import cthree.user.flypass.utils.Utils

private const val TAG = "HistorySummaryFragment"

class HistorySummaryFragment : Fragment() {

    private lateinit var binding: FragmentHistorySummaryBinding
    private lateinit var booking: Booking

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistorySummaryBinding.inflate(layoutInflater, container ,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getArgs()
        setupToolbar()
        setViews()
        setBottomNav()
        binding.cvHistoryDetail.setOnClickListener {

        }
    }

    private fun setViews() {
        binding.tvDepartCity.text = booking.depFlight.duration
        binding.bookingStatus.text = booking.bookingStatus.name
        binding.tvDepartCity.text = booking.depFlight.departureAirport.city
        binding.tvArriveCity.text= booking.depFlight.arrivalAirport.city
        binding.tvBaggagePrice.text = Utils.formattedMoney(booking.totalPassengerBaggagePrice)
        binding.tvPassengerPrice.text = Utils.formattedMoney(booking.depFlight.price)
        binding.tvTotalPrice.text = Utils.formattedMoney(booking.totalPrice)
        binding.tvAirplaneName.text = booking.depFlight.airline.name
        binding.tvDepartTime.text = Utils.formattedTime(booking.depFlight.departureTime)
        binding.tvArriveTime.text = Utils.formattedTime(booking.depFlight.arrivalTime)
        binding.tvDepartDate.text = Utils.convertDateToDayDate(booking.depFlight.departureDate)
        binding.tvPassengerQty.text = booking.passengerQty.toString()
    }

    private fun getArgs(){
        val bundle = arguments ?: return
        val args = HistorySummaryFragmentArgs.fromBundle(bundle)
        booking = args.booking
        Log.d(TAG, "getArgs: $booking")
    }
    private fun setBottomNav(){
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.isVisible = false
    }
    private fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.title = "Booking Summary"
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }
    }

}