package cthree.admin.flypass.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import cthree.admin.flypass.R
import cthree.admin.flypass.databinding.FragmentDetailTicketBinding
import cthree.admin.flypass.models.ticketflight.Flight
import cthree.admin.flypass.models.update.ForUpdate
import cthree.admin.flypass.utils.SessionManager
import cthree.admin.flypass.viewmodels.TicketViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailTicketFragment : Fragment() {

    lateinit var binding: FragmentDetailTicketBinding
    private lateinit var detailTicket : Flight
    private lateinit var sessionManager: SessionManager
    private val ticketVM: TicketViewModel by viewModels()
    private var idTicket : Int = 0
    private lateinit var flightNumber : String
    private lateinit var departAirportCity : String
    private lateinit var arriveAirportCity : String
    private lateinit var airlineName : String
    private lateinit var airplaneType : String
    private lateinit var calendarDepart : String
    private lateinit var calendarArrival : String
    private lateinit var timeDepart : String
    private lateinit var timeArrival : String
    private var price : Int = 0
    private var spSeatClass : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(requireContext())
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

        val token = sessionManager.getToken()
        idTicket = detailTicket.id

        flightNumber = detailTicket.flightCode
        departAirportCity = detailTicket.departureAirport.city
        arriveAirportCity = detailTicket.arrivalAirport.city
        airlineName = detailTicket.airline.name
        airplaneType = detailTicket.airplane.model
        calendarDepart = detailTicket.departureDate
        calendarArrival = detailTicket.arrivalDate
        timeDepart = detailTicket.departureTime
        timeArrival = detailTicket.arrivalTime
        price = detailTicket.price
        spSeatClass = detailTicket.flightClass.id

        binding.btnEdit.setOnClickListener {
            ticketVM.saveDataForUpdatePrefs(ForUpdate(flightNumber, departAirportCity, arriveAirportCity, airlineName, airplaneType, calendarDepart, calendarArrival, timeDepart, timeArrival, price, spSeatClass))
            Navigation.findNavController(binding.root).navigate(R.id.action_detailTicketFragment_to_updateTicketFragment)
//            val directions = DetailTicketFragmentDirections.actionDetailTicketFragmentToUpdateTicketFragment(detailTicket)
//            findNavController().navigate(directions)
        }

        binding.btnDelete.setOnClickListener {
            ticketVM.callDeleteTicket("Bearer ${token!!.trim()}", idTicket)
            Toast.makeText(requireContext(), "Delete Ticket Success", Toast.LENGTH_SHORT).show()
            Navigation.findNavController(binding.root).popBackStack()
        }
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
        binding.tvBaggage.setText("Baggage $baggage kg")
        binding.tvPrice.setText(detailTicket.price.toString())
        Glide.with(this).load(detailTicket.airline.image).into(binding.ivLogoAirline)
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