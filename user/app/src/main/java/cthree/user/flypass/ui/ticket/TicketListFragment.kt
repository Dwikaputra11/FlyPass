package cthree.user.flypass.ui.ticket

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.androidbolts.topsheet.TopSheetBehavior
import cthree.user.flypass.R
import cthree.user.flypass.databinding.FragmentTicketListBinding

class TicketListFragment : Fragment() {

    private lateinit var binding: FragmentTicketListBinding
    private lateinit var topSheetBehavior: TopSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTicketListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_ios_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        topSheetBehavior = TopSheetBehavior.from(binding.topSheetContainer.topSheet)
        topSheetBehavior.state = TopSheetBehavior.STATE_COLLAPSED
        binding.toolbarLayout.ivDropDown.setOnClickListener {
            if(binding.toolbarLayout.ivDropDown.isChecked)
                topSheetBehavior.state = TopSheetBehavior.STATE_EXPANDED
            else
                topSheetBehavior.state = TopSheetBehavior.STATE_COLLAPSED

        }
        binding.topSheetContainer.swRoundTrip.setOnClickListener {
            binding.topSheetContainer.tvArrivalDate.isVisible = binding.topSheetContainer.swRoundTrip.isChecked
            binding.topSheetContainer.etArrivalDate.isVisible = binding.topSheetContainer.swRoundTrip.isChecked
        }
        binding.topSheetContainer.btnSearch.setOnClickListener {
            topSheetBehavior.state = TopSheetBehavior.STATE_COLLAPSED
            binding.toolbarLayout.ivDropDown.isChecked = false
        }
    }
}