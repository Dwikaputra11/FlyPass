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
import cthree.admin.flypass.models.ticketflight.Flight
import cthree.admin.flypass.utils.Constants
import cthree.admin.flypass.viewmodels.TicketViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class UpdateTicketFragment : Fragment() {

    lateinit var binding : FragmentUpdateTicketBinding
    private lateinit var detailTicket : Flight
    private val ticketVM: TicketViewModel by viewModels()
    val seatClass = arrayOf("Economy", "Executive", "Bussiness")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        setViews()

        val arrayAdapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, seatClass)
        binding.spSeatClass.adapter = arrayAdapter
        binding.spSeatClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Toast.makeText(requireContext(), "You Clicked " +seatClass[position], Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

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
            bundle.putString(Constants.FLIGHT_DIR, Constants.DEPART_AIRPORT)
            Navigation.findNavController(binding.root).navigate(R.id.action_addTicketFragment_to_searchAirportFragment, bundle)
        }
    }

    private fun setViews() {

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