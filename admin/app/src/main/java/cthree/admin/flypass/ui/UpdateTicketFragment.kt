package cthree.admin.flypass.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import cthree.admin.flypass.R
import cthree.admin.flypass.databinding.FragmentUpdateTicketBinding
import cthree.admin.flypass.models.postticket.TicketDataClass
import cthree.admin.flypass.models.ticketflight.Flight
import cthree.admin.flypass.utils.Constants
import cthree.admin.flypass.utils.SessionManager
import cthree.admin.flypass.viewmodels.TicketViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class UpdateTicketFragment : Fragment() {

    val seatClass = arrayOf("Economy", "Executive", "Bussiness")
    lateinit var binding : FragmentUpdateTicketBinding
//    private lateinit var detailTicket : Flight
    private val ticketVM: TicketViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private var idTicket : Int = 0
    private lateinit var flightNumber : String
    private var departAirportId : Int = 0
    private var arriveAirportId : Int = 0
    private var airlineId : Int = 0
    private var airplaneId : Int = 0
    private lateinit var calendarDepart : String
    private lateinit var calendarArrival : String
    private lateinit var timeDepart : String
    private lateinit var timeArrival : String
    private lateinit var price : String
    private var baggage : Int = 20
    private var isAvailable : Boolean = true
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
        binding = FragmentUpdateTicketBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
//        getArgs()
        setViews()

        val token = sessionManager.getToken()

        binding.btnUpdateTicket.setOnClickListener {
            flightNumber = binding.etFlightNumber.text.toString()
            calendarDepart = binding.etDateDeparture.text.toString()
            calendarArrival = binding.etDateArrival.text.toString()
            timeDepart = binding.etTimeDeparture.text.toString()
            timeArrival = binding.etTimeArrival.text.toString()
            price = binding.etPrice.text.toString()
            spSeatClass = (binding.spSeatClass.selectedItemPosition+1).toString().toInt()

            ticketVM.putApiTicket("Bearer ${token!!.trim()}", detailTicket.id, TicketDataClass(flightNumber, airlineId, airplaneId, departAirportId, arriveAirportId,
                calendarDepart, timeDepart, calendarArrival, timeArrival, price.toInt(), spSeatClass, baggage, isAvailable)
            )
        }

        val arrayAdapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, seatClass)
        binding.spSeatClass.adapter = arrayAdapter
        binding.spSeatClass.selectedItemPosition+1

        val calendar = Calendar.getInstance()
        val datePickerDeparture = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLableDepart(calendar)
        }

        val datePickerArrival = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLableArrive(calendar)
        }

        binding.etDateDeparture.setOnClickListener {
            DatePickerDialog(requireActivity(), datePickerDeparture, calendar.get(Calendar.YEAR), calendar.get(
                Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.etDateArrival.setOnClickListener {
            DatePickerDialog(requireActivity(), datePickerArrival, calendar.get(Calendar.YEAR), calendar.get(
                Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.etTimeDeparture.setOnClickListener {
            openTimePickerDepart()
        }

        binding.etTimeArrival.setOnClickListener {
            openTimePickerArrive()
        }

        binding.etFrom.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FLIGHT_DIR, Constants.DEPART_AIRPORT_UPDATE)
            Navigation.findNavController(binding.root).navigate(R.id.action_updateTicketFragment_to_searchAirportFragment, bundle)
        }
        binding.etTo.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FLIGHT_DIR, Constants.ARRIVE_AIRPORT_UPDATE)
            Navigation.findNavController(binding.root).navigate(R.id.action_updateTicketFragment_to_searchAirportFragment, bundle)
        }
        binding.etAirlineName.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FLIGHT_DIR, Constants.AIRLINE_NAME_UPDATE)
            Navigation.findNavController(binding.root).navigate(R.id.action_updateTicketFragment_to_searchAirlineFragment, bundle)
        }
        binding.etAirplaneType.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FLIGHT_DIR, Constants.AIRPLANE_TYPE_UPDATE)
            Navigation.findNavController(binding.root).navigate(R.id.action_updateTicketFragment_to_searchAirplaneFragment, bundle)
        }
    }

//    private fun getArgs() {
//        val bundle = arguments
//        if(bundle == null){
//            return
//        }
//        val args = UpdateTicketFragmentArgs.fromBundle(bundle)
//        detailTicket = args.detailTicket
//    }

    private fun setViews() {
        ticketVM.dataUser.observe(viewLifecycleOwner){
            binding.etFlightNumber.setText(it.flightCode)
            binding.etAirlineName.setText(it.airlineName)
            binding.etAirplaneType.setText(it.airplaneModel)
            binding.etFrom.setText(it.departAirportCity)
            binding.etDateDeparture.setText(it.calendarDepart)
            binding.etTimeDeparture.setText(it.timeDepart)
            binding.etTo.setText(it.arriveAirportCity)
            binding.etDateArrival.setText(it.calendarArrival)
            binding.etTimeArrival.setText(it.timeArrival)
            binding.etPrice.setText(it.price.toString())
            binding.spSeatClass.setSelection(it.spSeatClass-1)
        }
//        binding.etFlightNumber.setText(detailTicket.flightCode)
//        binding.etAirlineName.setText(detailTicket.airline.name)
//        binding.etAirplaneType.setText(detailTicket.airplane.model)
//        binding.etFrom.setText("${detailTicket.departureAirport.city}, ${detailTicket.departureAirport.country}")
//        binding.etDateDeparture.setText(detailTicket.departureDate)
//        binding.etTimeDeparture.setText(detailTicket.departureTime)
//        binding.etTo.setText("${detailTicket.arrivalAirport.city}, ${detailTicket.arrivalAirport.country}")
//        binding.etDateArrival.setText(detailTicket.arrivalDate)
//        binding.etTimeArrival.setText(detailTicket.arrivalTime)
//        binding.spSeatClass.setSelection(detailTicket.flightClass.id)
//        binding.etPrice.setText(detailTicket.price.toString())
    }

    private fun openTimePickerArrive() {
        val isSystem24Hour = DateFormat.is24HourFormat(requireContext())
        val clockFormat = if(isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Set Arrival Time")
            .build()
        picker.show(childFragmentManager, "TAG")

        picker.addOnPositiveButtonClickListener {
            Log.d("Update Ticket Fragment", "POSITIVE")
            val hourArrive = picker.hour
            val minuteArrive = picker.minute
            Log.d("Update Ticket Fragment", "$hourArrive:$minuteArrive")
            binding.etTimeArrival.setText("$hourArrive:$minuteArrive:00")
        }
        picker.addOnNegativeButtonClickListener {
            Log.d("Update Ticket Fragment", "NEGATIVE")
        }
        picker.addOnCancelListener {
            Log.d("Update Ticket Fragment", "CANCEL")
        }
        picker.addOnDismissListener {
            Log.d("Update Ticket Fragment", "DISMISS")
        }
    }

    private fun openTimePickerDepart() {
        val isSystem24Hour = DateFormat.is24HourFormat(requireContext())
        val clockFormat = if(isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Set Departure Time")
            .build()
        picker.show(childFragmentManager, "TAG")

        picker.addOnPositiveButtonClickListener {
            Log.d("Update Ticket Fragment", "POSITIVE")
            val hourDepart = picker.hour
            val minuteDepart = picker.minute
            Log.d("Update Ticket Fragment", "$hourDepart:$minuteDepart")
            binding.etTimeDeparture.setText("$hourDepart:$minuteDepart:00")
        }
        picker.addOnNegativeButtonClickListener {
            Log.d("Update Ticket Fragment", "NEGATIVE")
        }
        picker.addOnCancelListener {
            Log.d("Update Ticket Fragment", "CANCEL")
        }
        picker.addOnDismissListener {
            Log.d("Update Ticket Fragment", "DISMISS")
        }
    }

    private fun updateLableDepart(calendar: Calendar) {
        val format = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(format, Locale.US)
        binding.etDateDeparture.setText(sdf.format(calendar.time))
    }

    private fun updateLableArrive(calendar: Calendar) {
        val format = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(format, Locale.US)
        binding.etDateArrival.setText(sdf.format(calendar.time))
    }

    private fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.title = "Update Ticket"
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }
    }
}