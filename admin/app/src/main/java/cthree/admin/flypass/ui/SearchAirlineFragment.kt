package cthree.admin.flypass.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import cthree.admin.flypass.R
import cthree.admin.flypass.adapter.AirlineSearchAdapter
import cthree.admin.flypass.databinding.FragmentSearchAirlineBinding
import cthree.admin.flypass.models.airline.Airline
import cthree.admin.flypass.utils.Constants
import cthree.admin.flypass.utils.SessionManager
import cthree.admin.flypass.utils.Utils
import cthree.admin.flypass.viewmodels.AirlineViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchAirlineFragment : Fragment() {

    lateinit var binding : FragmentSearchAirlineBinding
    private val airlineViewModel : AirlineViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private lateinit var args: String
    private val airlineAdapter = AirlineSearchAdapter()
    private val searchAdapter = AirlineSearchAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchAirlineBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar()

        args = requireArguments().getString(Constants.FLIGHT_DIR).toString()

        setAirlineList()

        binding.toolbarLayout.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.progressBar.isVisible = true
                if(query != null) {
                    val searchQuery = Utils.getCountryCode(query) ?: query
                    airlineViewModel.searchAirline(searchQuery).observe(viewLifecycleOwner) {
                        binding.existAirlineList.root.isVisible = false
                        binding.progressBar.isVisible = false
                        if (it != null) {
                            binding.rvSearchResult.isVisible = true
                            searchAdapter.submitList(it)
                        }
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                binding.progressBar.isVisible                   = true
                binding.existAirlineList.root.isVisible         = false
                binding.rvSearchResult.isVisible                = false
                if(newText == null || newText.isEmpty()){
                    binding.existAirlineList.root.isVisible     = true
                    binding.rvSearchResult.isVisible            = false
                    binding.progressBar.isVisible               = false
                }else{
//                    val searchQuery = Utils.getCountryCode(newText) ?: newText
//                    airportViewModel.searchAirport(searchQuery).observe(viewLifecycleOwner) {
//                        Log.d(TAG, "onQueryTextSubmit: $it")
//                        binding.progressBar.isVisible = false
//                        if (it != null) {
//                            binding.rvSearchResult.isVisible = true
//                            searchAdapter.submitList(it)
//                        }
//                    }
                }
                return true
            }

        })
    }

    private fun setAirlineList() {
        binding.rvSearchResult.adapter                  = searchAdapter
        binding.rvSearchResult.layoutManager            = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        searchAdapter.senOnItemClickListener(object : AirlineSearchAdapter.OnItemClickListener{
            override fun onItemClick(airline: Airline) {
                setNavigation(airline)
            }
        })
        airlineViewModel.getAllAirlineFromDB().observe(viewLifecycleOwner){
            airlineAdapter.submitList(it)
        }
        binding.existAirlineList.rvListAirline.adapter        = airlineAdapter
        binding.existAirlineList.rvListAirline.layoutManager  = object : LinearLayoutManager(requireContext()){
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        airlineAdapter.senOnItemClickListener(object : AirlineSearchAdapter.OnItemClickListener{
            override fun onItemClick(airline: Airline) {
                setNavigation(airline)
            }
        })
    }

    fun setNavigation(airline: Airline){
        if(args == Constants.AIRLINE_NAME){
            airlineViewModel.addAirlinePrefs(airline)
            Navigation.findNavController(binding.root).navigate(R.id.action_searchAirlineFragment_to_addTicketFragment)
        }else{
            airlineViewModel.addAirlinePrefs(airline)
            Navigation.findNavController(binding.root).navigate(R.id.action_searchAirlineFragment_to_updateTicketFragment)
        }
    }

    private fun setToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }
    }
}