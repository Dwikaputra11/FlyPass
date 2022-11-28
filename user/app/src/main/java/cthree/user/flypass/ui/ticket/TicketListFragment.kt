package cthree.user.flypass.ui.ticket

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidbolts.topsheet.TopSheetBehavior
import cthree.user.flypass.R
import cthree.user.flypass.adapter.TicketListAdapter
import cthree.user.flypass.data.DummyData
import cthree.user.flypass.data.Ticket
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
            if(binding.toolbarLayout.ivDropDown.isChecked){
                binding.topSheetBg.isVisible = true
                topSheetBehavior.state = TopSheetBehavior.STATE_EXPANDED
            }
            else{
                binding.topSheetBg.isVisible = false
                topSheetBehavior.state = TopSheetBehavior.STATE_COLLAPSED
            }
        }
        binding.topSheetContainer.swRoundTrip.setOnClickListener {
            binding.topSheetContainer.tvArrivalDate.isVisible = binding.topSheetContainer.swRoundTrip.isChecked
            binding.topSheetContainer.etArrivalDate.isVisible = binding.topSheetContainer.swRoundTrip.isChecked
        }
        binding.topSheetContainer.btnSearch.setOnClickListener {
            topSheetBehavior.state = TopSheetBehavior.STATE_COLLAPSED
            binding.toolbarLayout.ivDropDown.isChecked = false
            binding.topSheetBg.isVisible = false
        }
        binding.topSheetBg.setOnClickListener {
            topSheetBehavior.state = TopSheetBehavior.STATE_COLLAPSED
            binding.toolbarLayout.ivDropDown.isChecked = false
            binding.topSheetBg.isVisible = false
        }
        setAdapter()
    }

    private fun setAdapter(){
        val adapter = TicketListAdapter(DummyData.ticketList)
        binding.rvTicketList.adapter = adapter
        binding.rvTicketList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter.setOnItemClickListener(object : TicketListAdapter.OnItemClickListener{
            override fun onItemClick(ticket: Ticket) {
                val detailFragment = TicketDetailFragment(ticket)
                detailFragment.show(requireActivity().supportFragmentManager, detailFragment.tag)
            }

        })
    }
}