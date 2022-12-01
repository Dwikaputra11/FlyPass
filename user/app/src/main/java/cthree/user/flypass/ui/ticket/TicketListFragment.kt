package cthree.user.flypass.ui.ticket

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidbolts.topsheet.TopSheetBehavior
import cthree.user.flypass.R
import cthree.user.flypass.adapter.TicketListAdapter
import cthree.user.flypass.data.DummyData
import cthree.user.flypass.data.Ticket
import cthree.user.flypass.databinding.FragmentTicketListBinding

private const val TAG = "TicketListFragment"

class TicketListFragment : Fragment() {

    private lateinit var binding: FragmentTicketListBinding
    private lateinit var topSheetBehavior: TopSheetBehavior<View>
    private var isRoundTrip = true;
    private var singleDepart = "Jakarta"
    private var singleArrive = "Bali"
    private var singleDepartDate = "Wed, 30 Nov"
    private var roundDepart = "Bali"
    private var roundArrive = "Jakarta"
    private var roundDepartDate = "Sat, 3 Dec"
    private var seatClass = "Economy"
    private val adapter: TicketListAdapter = TicketListAdapter()

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
        setToolbar()
        setTopSheetDialog()
        setAdapter()
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


    private fun setTopSheetDialog(){
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
    }

    private fun setAdapter(){
        binding.rvTicketList.adapter = adapter
        binding.rvTicketList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter.submitList(DummyData.firstTicketList)
        adapter.setOnItemClickListener(object : TicketListAdapter.OnItemClickListener{
            override fun onItemClick(view: View, ticket: Ticket) {
                when(view.id){
                    R.id.btnDetail ->{
                        val detailFragment = TicketDetailFragment(ticket)
                        detailFragment.show(requireActivity().supportFragmentManager, detailFragment.tag)
                    }
                    R.id.container ->{
                        Log.d(TAG, "onItemClick: CardView Clicked")
                        if(isRoundTrip){
                            Navigation.findNavController(binding.root).navigate(R.id.action_ticketListFragment_to_ticketRoundTripListFragment)
                        }else{
                            Navigation.findNavController(binding.root).navigate(R.id.action_ticketListFragment_to_flightConfirmationFragment)
                        }
                    }
                }
            }
        })
    }
}