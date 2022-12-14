package cthree.user.flypass.ui.mybooking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cthree.user.flypass.R
import cthree.user.flypass.databinding.FragmentHistoryDetailBinding
import cthree.user.flypass.models.booking.bookings.Booking

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
    }

    private fun setViews() {

    }

    private fun getArgs(){
        val bundle = arguments ?: return
        val args = HistoryDetailFragmentArgs.fromBundle(bundle)
        booking = args.booking
    }
}