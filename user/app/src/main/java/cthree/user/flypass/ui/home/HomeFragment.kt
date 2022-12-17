package cthree.user.flypass.ui.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import androidx.work.WorkInfo
import com.google.android.material.bottomnavigation.BottomNavigationView
import cthree.user.flypass.R
import cthree.user.flypass.adapter.HighlightTopicAdapter
import cthree.user.flypass.adapter.RecentSearchAdapter
import cthree.user.flypass.data.DummyData
import cthree.user.flypass.data.RecentSearch
import cthree.user.flypass.databinding.FragmentHomeBinding
import cthree.user.flypass.models.airport.Airport
import cthree.user.flypass.utils.Constants
import cthree.user.flypass.utils.SessionManager
import cthree.user.flypass.utils.Utils
import cthree.user.flypass.viewmodels.AirportViewModel
import cthree.user.flypass.viewmodels.RecentSearchViewModel
import cthree.user.flypass.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "HomeFragment"

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var sessionManager: SessionManager
    private val airportViewModel: AirportViewModel by viewModels()
    private val recentSearchViewModel: RecentSearchViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val calDep: Calendar = Calendar.getInstance()
    private val calArr: Calendar = Calendar.getInstance()
    private lateinit var departIata: String
    private lateinit var arriveIata: String
    private var departCity: String? = null
    private var arriveCity: String? = null
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private lateinit var dateField: String
    private val seatClassFragment = SeatClassFragment()
    private val passengerAmountFragment = PassengerAmountFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: Started")
        sessionManager = SessionManager(requireContext())
        onBackPressedCallback = requireActivity().onBackPressedDispatcher.addCallback(this) {
        }
        super.onCreate(savedInstanceState)
    }

//    fun onBackPressed(): Boolean{
//        return false
//    }

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
        setBottomNav()
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)

        binding.flightPay.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_flightPayFragment)
        }

        binding.topup.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_topUpFragment)
        }

        binding.swRoundTrip.setOnClickListener {
            binding.etArriveDate.isVisible = binding.swRoundTrip.isChecked
            binding.tvArrivalDate.isVisible = binding.swRoundTrip.isChecked
        }

        // set date dialog listener
        val dateDepListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calDep.set(Calendar.YEAR, year)
                calDep.set(Calendar.MONTH, monthOfYear)
                calDep.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                // after set we have to update in view
                updateDate()
            }
        val dateArrListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calArr.set(Calendar.YEAR, year)
                calArr.set(Calendar.MONTH, monthOfYear)
                calArr.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                // after set we have to update in view
                updateDate()
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
            dateField = Constants.DEPART_DATE
            DatePickerDialog(
                requireActivity(),
                dateDepListener,
                calDep.get(Calendar.YEAR),
                calDep.get(Calendar.MONTH),
                calDep.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.etArriveDate.setOnClickListener {
            dateField = Constants.ARRIVE_DATE
            DatePickerDialog(
                requireActivity(),
                dateArrListener,
                calArr.get(Calendar.YEAR),
                calArr.get(Calendar.MONTH),
                calArr.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.btnSearch.setOnClickListener {
            navigateToTicketList()
            userViewModel.clearAirportSearch()
            sessionManager.setSeatClass(binding.etSeatClass.text.toString())
            sessionManager.setPassengerAmount(binding.etPassengers.text.toString().toInt())
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

        binding.etSeatClass.setOnClickListener {
            seatClassFragment.show(requireActivity().supportFragmentManager, seatClassFragment.tag)
        }

        seatClassFragment.setOnCLickListener(object : SeatClassFragment.OnClickListener{
            override fun onClick(seatClass: String) {
                binding.etSeatClass.setText(seatClass)
            }
        })

        binding.etPassengers.setOnClickListener {
            passengerAmountFragment.show(requireActivity().supportFragmentManager, passengerAmountFragment.tag)
        }
        passengerAmountFragment.setOnClickListener(object : PassengerAmountFragment.OnClickListener{
            override fun onClick(passenger: String) {
                binding.etPassengers.setText(passenger)
            }
        })

        binding.tvClearAll.setOnClickListener {
            recentSearchViewModel.deleteAllSearch()
        }
    }

    private fun navigateToTicketList() {
        val dateDep = Utils.reverseDateFormat(calDep)
        val dateArr = if(binding.etArriveDate.isVisible) Utils.reverseDateFormat(calArr) else null
        val arriveDateTv = if(binding.swRoundTrip.isChecked) binding.etArriveDate.text.toString() else null
        val recentSearch = RecentSearch(
            departDate = dateDep,
            arriveDate = dateArr,
            iataArriveAirport = arriveIata,
            iataDepartAirport = departIata,
            arriveCity = arriveCity ?: "City",
            departCity = departCity ?: "City",
            id = 0,
            seatClass = binding.etSeatClass.text.toString(),
            passengerAmount = binding.etPassengers.text.toString().toInt()
        )
        val directions = HomeFragmentDirections.actionHomeFragmentToTicketListFragment(
            search = recentSearch,
            arrDateTv = arriveDateTv,
            depDateTv = binding.etDepartureDate.text.toString(),
            isRoundtrip = binding.swRoundTrip.isChecked
        )
        recentSearchViewModel.insertSearch(recentSearch)
        findNavController().navigate(directions)
    }

    @SuppressLint("SetTextI18n")
    private fun setViews() {
        userViewModel.dataUser.observe(viewLifecycleOwner){
            if(it.arriveAirportCity.isNotEmpty()){
                arriveIata = it.arriveAirportIata
                arriveCity = it.arriveAirportCity
                binding.etToAirport.setText("${it.arriveAirportCity}, ${it.arriveAirportCountry}")
            }
            if(it.departAirportCity.isNotEmpty()){
                departIata = it.departAirportIata
                departCity = it.departAirportCity
                binding.etFromAirport.setText("${it.departAirportCity}, ${it.departAirportCountry}")
            }
        }
    }

    private fun setAdapter() {
        with(binding.vpHighlight){
            adapter = HighlightTopicAdapter(DummyData.highlightTopicItem)
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            offscreenPageLimit = 3
            setPageTransformer(MarginPageTransformer(50))
            binding.wormDot.attachTo(binding.vpHighlight)
            clipToPadding = false
            setPadding(10, 10, 10 ,0)
        }
        val recentAdapter = RecentSearchAdapter()
        recentSearchViewModel.getAllSearch().observe(viewLifecycleOwner){
            if(it.isEmpty()){
                recentAdapter.submitList(emptyList())
                binding.layoutRecentSearch.isVisible = false
            }else{
                recentAdapter.submitList(it)
                binding.layoutRecentSearch.isVisible = true
            }
        }
        with(binding.rvRecentSearch){
            adapter = recentAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            recentAdapter.setOnItemClickListener(object : RecentSearchAdapter.SetOnItemClickListener{
                override fun onItemClick(search: RecentSearch) {
                    Log.d(TAG, "search: $search")
                    val directions = HomeFragmentDirections.actionHomeFragmentToTicketListFragment(
                        search = search,
                        arrDateTv = search.arriveDate,
                        depDateTv = search.departDate,
                        isRoundtrip = search.arriveDate != null
                    )
                    findNavController().navigate(directions)
                }
            })
        }
    }

    private fun updateDate() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        if(dateField == Constants.DEPART_DATE)
            binding.etDepartureDate.setText(sdf.format(calDep.time))
        else
            binding.etArriveDate.setText(sdf.format(calArr.time))
    }

    private fun setBottomNav(){
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.isVisible = true
    }

}