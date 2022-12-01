package cthree.user.flypass.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import cthree.user.flypass.R
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

    }

}