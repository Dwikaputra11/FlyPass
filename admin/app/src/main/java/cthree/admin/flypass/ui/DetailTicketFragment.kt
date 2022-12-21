package cthree.admin.flypass.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import cthree.admin.flypass.R
import cthree.admin.flypass.databinding.FragmentDetailTicketBinding
import cthree.admin.flypass.models.ticketflight.Flight
import cthree.admin.flypass.viewmodels.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class DetailTicketFragment : Fragment() {

    lateinit var binding: FragmentDetailTicketBinding
    private val adminVM: AdminViewModel by viewModels()
    private lateinit var detailTicket : Flight

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailTicketBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()

        getArgs()
        setViews()
    }

    private fun getArgs() {
        val bundle = arguments
        if(bundle == null){
            return
        }
        val args = DetailTicketFragmentArgs.fromBundle(bundle)
        detailTicket = args.detailTicket
    }

    private fun setViews() {
        binding.tvDepature.setText(detailTicket.departureAirport.city)
        binding.tvFlight.setText(detailTicket.arrivalAirport.city)
        binding.tvNameAirline.setText(detailTicket.airline.name)
        binding.tvGate.setText(detailTicket.flightCode)
        binding.tvClass.setText(detailTicket.flightClass.name)
        binding.tvDepatureTime.setText(detailTicket.departureTime)
        binding.tvDepatureDetail.setText(detailTicket.departureAirport.city)
        binding.tvDepatureIata.setText(detailTicket.departureAirport.iata)
        binding.tvDepatureAirport.setText(detailTicket.departureAirport.name)
        binding.tvDepatureDate.setText(detailTicket.departureDate)
        binding.tvArrivalTime.setText(detailTicket.arrivalTime)
        binding.tvArrivalDetail.setText(detailTicket.arrivalAirport.city)
        binding.tvArrivalIata.setText(detailTicket.arrivalAirport.iata)
        binding.tvArrivalAirport.setText(detailTicket.arrivalAirport.name)
        binding.tvArrivalDate.setText(detailTicket.arrivalDate)
        binding.tvAirplane.setText(detailTicket.airplane.model)
        var baggage = detailTicket.baggage.toString()
        binding.tvBaggage.setText("Baggage {$baggage} kg")
        binding.tvPrice.setText(detailTicket.price.toString())
    }

    private fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.title = "Detail Ticket"
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }
    }
}