package cthree.user.flypass.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import cthree.user.flypass.R
import cthree.user.flypass.adapter.HighlightTopicAdapter
import cthree.user.flypass.data.DummyData
import cthree.user.flypass.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnSearch.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_ticketListFragment)
        }

        binding.etFromAirport.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_searchAirportFragment)
        }

        binding.etToAirport.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_searchAirportFragment)
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