package cthree.user.flypass.ui.mybooking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cthree.user.flypass.R
import cthree.user.flypass.databinding.FragmentHistorySummaryBinding
import cthree.user.flypass.models.booking.bookings.Booking

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
        setViews()
    }

    private fun setViews() {
        binding.tvDepartCity.text = booking.flightBook.duration
    }

    private fun getArgs(){
        val bundle = arguments ?: return
        val args = HistorySummaryFragmentArgs.fromBundle(bundle)
        booking = args.booking
    }
}