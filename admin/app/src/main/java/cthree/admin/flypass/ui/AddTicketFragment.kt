package cthree.admin.flypass.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import cthree.admin.flypass.R
import cthree.admin.flypass.databinding.FragmentAddTicketBinding
import cthree.admin.flypass.models.postticket.TicketDataClass
import cthree.admin.flypass.utils.Constants
import cthree.admin.flypass.utils.SessionManager
import cthree.admin.flypass.viewmodels.AirportViewModel
import cthree.admin.flypass.viewmodels.TicketViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddTicketFragment : Fragment() {

    val seatClass = arrayOf("Economy", "Executive", "Bussiness")
    lateinit var binding : FragmentAddTicketBinding
    private val ticketVM : TicketViewModel by viewModels()
    private val airportVM : AirportViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private lateinit var flightNumber : String
    private var departAirportId : Int = 0
    private var arriveAirportId : Int = 0
    private var airlineId : Int = 0
    private var airplaneId : Int = 0
    private lateinit var calendarDepart : String
    private lateinit var calendarArrival : String
    private lateinit var timeDepart : String
    private lateinit var timeArrival : String
    private var price : Int = 0
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
        binding = FragmentAddTicketBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setViews()

        val token = sessionManager.getToken()

        binding.btnAddTicket.setOnClickListener {
            flightNumber = binding.etFlightNumber.text.toString()
            calendarDepart = binding.etDateDeparture.text.toString()
            calendarArrival = binding.etDateArrival.text.toString()
            timeDepart = binding.etTimeDeparture.text.toString()
            timeArrival = binding.etTimeArrival.text.toString()
            price = binding.etPrice.text.toString().toInt()
            spSeatClass = (binding.spSeatClass.selectedItemPosition+1).toString().toInt()

            ticketVM.postApiTicket("Bearer ${token!!.trim()}", TicketDataClass(flightNumber, airlineId, airplaneId, departAirportId, arriveAirportId,
            calendarDepart, timeDepart, calendarArrival, timeArrival, price, spSeatClass, baggage, isAvailable)
            )
        }

        val arrayAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, seatClass)
        binding.spSeatClass.adapter = arrayAdapter

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
            DatePickerDialog(requireActivity(), datePickerDeparture, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.etDateArrival.setOnClickListener {
            DatePickerDialog(requireActivity(), datePickerArrival, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
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
            bundle.putString(Constants.FLIGHT_DIR, Constants.DEPART_AIRPORT)
            Navigation.findNavController(binding.root).navigate(R.id.action_addTicketFragment_to_searchAirportFragment, bundle)
        }
        binding.etTo.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FLIGHT_DIR, Constants.ARRIVE_AIRPORT)
            Navigation.findNavController(binding.root).navigate(R.id.action_addTicketFragment_to_searchAirportFragment, bundle)
        }
        binding.etAirlineName.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FLIGHT_DIR, Constants.AIRLINE_NAME)
            Navigation.findNavController(binding.root).navigate(R.id.action_addTicketFragment_to_searchAirlineFragment, bundle)
        }
        binding.etAirplaneType.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FLIGHT_DIR, Constants.AIRPLANE_TYPE)
            Navigation.findNavController(binding.root).navigate(R.id.action_addTicketFragment_to_searchAirplaneFragment, bundle)
        }
    }

    private fun openTimePickerArrive() {
        val isSystem24Hour = is24HourFormat(requireContext())
        val clockFormat = if(isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Set Arrival Time")
            .build()
        picker.show(childFragmentManager, "TAG")

        picker.addOnPositiveButtonClickListener {
            Log.d("Add Ticket Fragment", "POSITIVE")
            val hourArrive = picker.hour
            val minuteArrive = picker.minute
            Log.d("Add Ticket Fragment", "$hourArrive:$minuteArrive")
            binding.etTimeArrival.setText("$hourArrive:$minuteArrive:00")
        }
        picker.addOnNegativeButtonClickListener {
            Log.d("Add Ticket Fragment", "NEGATIVE")
        }
        picker.addOnCancelListener {
            Log.d("Add Ticket Fragment", "CANCEL")
        }
        picker.addOnDismissListener {
            Log.d("Add Ticket Fragment", "DISMISS")
        }
    }

    private fun openTimePickerDepart() {
        val isSystem24Hour = is24HourFormat(requireContext())
        val clockFormat = if(isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Set Departure Time")
            .build()
        picker.show(childFragmentManager, "TAG")

        picker.addOnPositiveButtonClickListener {
            Log.d("Add Ticket Fragment", "POSITIVE")
            val hourDepart = picker.hour
            val minuteDepart = picker.minute
            Log.d("Add Ticket Fragment", "$hourDepart:$minuteDepart")
            binding.etTimeDeparture.setText("$hourDepart:$minuteDepart:00")
        }
        picker.addOnNegativeButtonClickListener {
            Log.d("Add Ticket Fragment", "NEGATIVE")
        }
        picker.addOnCancelListener {
            Log.d("Add Ticket Fragment", "CANCEL")
        }
        picker.addOnDismissListener {
            Log.d("Add Ticket Fragment", "DISMISS")
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

    private fun setViews(){
        airportVM.dataUser.observe(viewLifecycleOwner){
            if (it.departAirportCity.isNotEmpty()){
                binding.etFrom.setText("${it.departAirportCity}, ${it.departAirportCountry}")
                departAirportId = it.departAirportId
            }
            if (it.arriveAirportCity.isNotEmpty()){
                binding.etTo.setText("${it.arriveAirportCity}, ${it.arriveAirportCountry}")
                arriveAirportId = it.arriveAirportId
            }
            if (it.airlineName.isNotEmpty()){
                binding.etAirlineName.setText(it.airlineName)
                airlineId = it.airlineId
            }
            if (it.airplaneModel.isNotEmpty()){
                binding.etAirplaneType.setText(it.airplaneModel)
                airplaneId = it.airplaneId
            }
        }
    }

    private fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.title = "Add Ticket"
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }
    }
}