package cthree.user.flypass.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import cthree.user.flypass.R
import cthree.user.flypass.adapter.HighlightTopicAdapter
import cthree.user.flypass.data.DummyData
import cthree.user.flypass.databinding.FragmentHomeBinding
import cthree.user.flypass.utils.Constants
import cthree.user.flypass.utils.SessionManager
import cthree.user.flypass.viewmodels.FlightViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var sessionManager: SessionManager
    private val flightViewModel: FlightViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
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

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val departValue = sessionManager.getSelectedAirport(Constants.DEPART_AIRPORT)
        val arriveValue = sessionManager.getSelectedAirport(Constants.ARRIVE_AIRPORT)
        if(departValue.city != Constants.DEPART_DEFAULT_VAL){
            binding.etFromAirport.setText("${departValue.city}, ${departValue.country}")
        }

        if(arriveValue.city != Constants.ARRIVE_DEFAULT_VAL){
            binding.etToAirport.setText("${arriveValue.city}, ${arriveValue.country}")
        }

        binding.btnSearch.setOnClickListener {
            sessionManager.clearAirport()
            Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_ticketListFragment)
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

        binding.vpHighlight.adapter = HighlightTopicAdapter(DummyData.highlightTopicItem)
        binding.vpHighlight.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpHighlight.offscreenPageLimit = 3
        binding.vpHighlight.setPageTransformer(MarginPageTransformer(50))
        binding.vpHighlight.beginFakeDrag()
        binding.wormDot.attachTo(binding.vpHighlight)
        binding.vpHighlight.clipToPadding = false
        binding.vpHighlight.setPadding(10, 10, 10 ,0)
    }

}