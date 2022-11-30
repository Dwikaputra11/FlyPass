package cthree.user.flypass.ui.ticket

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import cthree.user.flypass.R
import cthree.user.flypass.data.Ticket
import cthree.user.flypass.databinding.FragmentTicketDetailBinding

class TicketDetailFragment(val ticket: Ticket) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentTicketDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTicketDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setViews()
    }

    private fun setViews() {
        binding.aitaDepartAirport.text = ticket.aitaDeparture
        binding.aitaArriveAirport.text = ticket.aitaArrival
//        binding.flightCode.text = ticket.flightCode
//        binding.tvAirplaneName.text = ticket.airplaneName
        binding.seatClass.text = ticket.seatClass
        binding.tvArriveTime.text = ticket.arrivalTime
        binding.tvDepartTime.text = ticket.departureTime
    }
}