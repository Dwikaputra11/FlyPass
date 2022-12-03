package cthree.user.flypass.ui.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import androidx.work.WorkInfo
import cthree.user.flypass.R
import cthree.user.flypass.adapter.HighlightTopicAdapter
import cthree.user.flypass.data.DummyData
import cthree.user.flypass.databinding.FragmentHomeBinding
import cthree.user.flypass.models.airport.Airport
import cthree.user.flypass.utils.Constants
import cthree.user.flypass.utils.SessionManager
import cthree.user.flypass.viewmodels.AirportViewModel
import cthree.user.flypass.viewmodels.FlightViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "HomeFragment"

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var sessionManager: SessionManager
    private val airportViewModel: AirportViewModel by viewModels()
    private val cal: Calendar = Calendar.getInstance()
    private lateinit var departValue: Airport
    private lateinit var arriveValue: Airport

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: Started")
        sessionManager = SessionManager(requireContext())
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: Started")
        setViews()
        setAdapter()

        // set date dialog listener
        val dateDepartListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                // after set we have to update in view
                updateDepartDate()
            }

        if(sessionManager.getIsFirstInstall()){
            Log.d(TAG, "onViewCreated: first")
            airportViewModel.fetchAirportData()
            airportViewModel.getAirportWorkerInfo().observe(viewLifecycleOwner){
                val workInfo = it[0]
                if(workInfo.state == WorkInfo.State.SUCCEEDED){
                    Log.d(TAG, "fetchAirport: Done")
                    sessionManager.setIsFirstInstall(false)
                }
            }
        }

        binding.etDepartureDate.setOnClickListener {
            DatePickerDialog(
                requireActivity(),
                dateDepartListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.btnSearch.setOnClickListener {
            val date = getDateFormat()
            val bundle = Bundle()
            sessionManager.clearAirport()
            bundle.putString(Constants.DEPART_DATE, date)
            bundle.putString(Constants.DEP_AIRPORT, departValue.iata)
            bundle.putString(Constants.ARR_AIRPORT, arriveValue.iata)
            Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_ticketListFragment, bundle)
        }

        binding.etFromAirport.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FLIGHT_DIR, Constants.DEPART_AIRPORT)
            Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_searchAirportFragment, bundle)
        }

        binding.etToAirport.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FLIGHT_DIR, Constants.ARRIVE_AIRPORT)
            Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_searchAirportFragment, bundle)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setViews() {
        departValue = sessionManager.getSelectedAirport(Constants.DEPART_AIRPORT)
        arriveValue = sessionManager.getSelectedAirport(Constants.ARRIVE_AIRPORT)
        if(departValue.city != Constants.DEPART_DEFAULT_VAL){
            binding.etFromAirport.setText("${departValue.city}, ${departValue.country}")
        }

        if(arriveValue.city != Constants.ARRIVE_DEFAULT_VAL){
            binding.etToAirport.setText("${arriveValue.city}, ${arriveValue.country}")
        }
    }

    private fun setAdapter() {
        binding.vpHighlight.adapter = HighlightTopicAdapter(DummyData.highlightTopicItem)
        binding.vpHighlight.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpHighlight.offscreenPageLimit = 3
        binding.vpHighlight.setPageTransformer(MarginPageTransformer(50))
        binding.vpHighlight.beginFakeDrag()
        binding.wormDot.attachTo(binding.vpHighlight)
        binding.vpHighlight.clipToPadding = false
        binding.vpHighlight.setPadding(10, 10, 10 ,0)
    }

    private fun updateDepartDate() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.etDepartureDate.setText(sdf.format(cal.time))
    }

    private fun updateArriveDate() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.etToAirport.setText(sdf.format(cal.time))
    }

    private fun getDateFormat(): String{
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        return sdf.format(cal.time)
    }

}