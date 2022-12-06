package cthree.user.flypass.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkInfo
import com.google.android.material.bottomnavigation.BottomNavigationView
import cthree.user.flypass.R
import cthree.user.flypass.adapter.AirportSearchAdapter
import cthree.user.flypass.data.DummyData
import cthree.user.flypass.databinding.FragmentSearchAirportBinding
import cthree.user.flypass.models.airport.Airport
import cthree.user.flypass.utils.Constants
import cthree.user.flypass.utils.SessionManager
import cthree.user.flypass.viewmodels.AirportViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "SearchAirportFragment"

@AndroidEntryPoint
class SearchAirportFragment : Fragment() {

    private lateinit var binding: FragmentSearchAirportBinding
    private val airportViewModel : AirportViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private lateinit var args: String
    private val recentSearchAdapter = AirportSearchAdapter()
    private val indoAirportAdapter = AirportSearchAdapter()
    private val japanAirportAdapter = AirportSearchAdapter()
    private val chinaAirportAdapter = AirportSearchAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchAirportBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setToolbar()
        setBottomNav()
        args = requireArguments().getString(Constants.FLIGHT_DIR).toString()

        setAirportList()

        binding.toolbarLayout.searchView.setOnQueryTextListener(object : OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d(TAG, "onQueryTextSubmit: $query")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
    }

    private fun setAirportList() {
        recentSearchAdapter.submitList(DummyData.frequentSearch)
        binding.existAirportList.rvRecentSearch.adapter = recentSearchAdapter
        binding.existAirportList.rvRecentSearch.layoutManager = object : LinearLayoutManager(requireContext()){
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        recentSearchAdapter.senOnItemClickListener(object : AirportSearchAdapter.OnItemClickListener{
            override fun onItemClick(airport: Airport) {
                setNavigation(airport)
            }
        })

        airportViewModel.searchAirport("ID").observe(viewLifecycleOwner){
            indoAirportAdapter.submitList(it)
        }
        binding.existAirportList.rvIndonesia.adapter = indoAirportAdapter
        binding.existAirportList.rvIndonesia.layoutManager = object : LinearLayoutManager(requireContext()){
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        indoAirportAdapter.senOnItemClickListener(object : AirportSearchAdapter.OnItemClickListener{
            override fun onItemClick(airport: Airport) {
                setNavigation(airport)
            }
        })

        airportViewModel.searchAirport("JP").observe(viewLifecycleOwner){
            japanAirportAdapter.submitList(it)
        }
        binding.existAirportList.rvJapan.adapter = japanAirportAdapter
        binding.existAirportList.rvJapan.layoutManager = object : LinearLayoutManager(requireContext()){
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        japanAirportAdapter.senOnItemClickListener(object : AirportSearchAdapter.OnItemClickListener{
            override fun onItemClick(airport: Airport) {
                setNavigation(airport)
            }
        })

        airportViewModel.searchAirport("CN").observe(viewLifecycleOwner){
            chinaAirportAdapter.submitList(it)
        }
        binding.existAirportList.rvChina.adapter = chinaAirportAdapter
        binding.existAirportList.rvChina.layoutManager = object : LinearLayoutManager(requireContext()){
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        chinaAirportAdapter.senOnItemClickListener(object : AirportSearchAdapter.OnItemClickListener{
            override fun onItemClick(airport: Airport) {
                setNavigation(airport)
            }
        })
    }

    fun setNavigation(airport: Airport){
        if(args == Constants.DEPART_AIRPORT){
            sessionManager.setSelectAirport(airport, Constants.DEPART_AIRPORT)
        }else{
            sessionManager.setSelectAirport(airport, Constants.ARRIVE_AIRPORT)
        }
        Navigation.findNavController(binding.root).navigate(R.id.action_searchAirportFragment_to_homeFragment)
    }

    private fun setToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }
    }
    private fun setBottomNav(){
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.isVisible = false
    }
}