package cthree.user.flypass.ui.ticket

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import cthree.user.flypass.R
import cthree.user.flypass.data.Ticket
import cthree.user.flypass.databinding.FragmentTicketDetailBinding
import cthree.user.flypass.models.flight.Flight
import cthree.user.flypass.utils.Utils

class TicketDetailFragment(val ticket: Flight) : BottomSheetDialogFragment() {

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
        binding.iataDepartAirport.text = ticket.departureAirport.iata
        binding.iataArriveAirport.text = ticket.arrivalAirport.iata
        binding.tvFlightCode.text = ticket.flightCode
        binding.tvAirplaneName.text = ticket.airline.name
        binding.seatClass.text = ticket.flightClass.name
        binding.tvArriveTime.text = Utils.formattedTime(ticket.arrivalTime)
        binding.tvDepartTime.text = Utils.formattedTime(ticket.departureTime)
        Glide.with(binding.root)
            .load(ticket.airline.image)
            .into(binding.airplaneLogo)
    }
}