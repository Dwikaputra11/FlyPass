package cthree.user.flypass.ui.ticket

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
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
import cthree.user.flypass.databinding.FragmentTicketRoundTripListBinding

private const val TAG = "TicketRoundTripListFragment"

class TicketRoundTripListFragment : Fragment() {

    private lateinit var binding: FragmentTicketRoundTripListBinding
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
        binding = FragmentTicketRoundTripListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setToolbar()
        setTopSheetDialog()
        setAdapter()
        roundTrip()
    }

    private fun setToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            Log.d(TAG, "setToolbar: Clicked")
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
        adapter.submitList(DummyData.secondTicketList)
        adapter.setOnItemClickListener(object : TicketListAdapter.OnItemClickListener{
            override fun onItemClick(view: View, ticket: Ticket) {
                when(view.id){
                    R.id.btnDetail ->{
                        val detailFragment = TicketDetailFragment(ticket)
                        detailFragment.show(requireActivity().supportFragmentManager, detailFragment.tag)
                    }
                    R.id.container ->{
                        Log.d(TAG, "onItemClick: CardView Clicked")
                        Navigation.findNavController(binding.root).navigate(R.id.action_ticketRoundTripListFragment_to_flightConfirmationFragment)
                    }
                }
            }
        })
    }

    private fun roundTrip(){
        binding.toolbarLayout.tvFrom.text           = roundDepart
        binding.toolbarLayout.tvTo.text             = roundArrive
        binding.toolbarLayout.tvDate.text           = roundDepartDate
        binding.toolbarLayout.tvSeatClass.text      = seatClass
    }
}