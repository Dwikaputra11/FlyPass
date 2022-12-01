package cthree.user.flypass.ui.booking

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import cthree.user.flypass.R
import cthree.user.flypass.databinding.FragmentFilghtConfirmationBinding

private const val TAG = "FlightConfirmationFragment"
class FlightConfirmationFragment : Fragment() {

    private lateinit var binding: FragmentFilghtConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFilghtConfirmationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupToolbar()
        binding.flightDetails.cbShowMore.setOnClickListener {
            binding.flightDetails.cbShowMore.text = if(binding.flightDetails.cbShowMore.isChecked) "Show Less" else "Show More"
            binding.flightShowDetails.root.isVisible = binding.flightDetails.cbShowMore.isChecked
        }
        binding.confirmLayout.btnConfirm.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_flightConfirmationFragment_to_bookingFragment)
        }
    }
    
    private fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.title = "Flight Confirmation"
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }
    }
}