package cthree.user.flypass.ui.ticket

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidbolts.topsheet.TopSheetBehavior
import com.google.android.material.bottomnavigation.BottomNavigationView
import cthree.user.flypass.R
import cthree.user.flypass.adapter.TicketListAdapter
import cthree.user.flypass.data.RecentSearch
import cthree.user.flypass.databinding.FragmentTicketListBinding
import cthree.user.flypass.models.flight.Flight
import cthree.user.flypass.utils.Constants
import cthree.user.flypass.utils.Utils
import cthree.user.flypass.viewmodels.FlightViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "TicketListFragment"

@AndroidEntryPoint
class TicketListFragment : Fragment() {

    private lateinit var binding: FragmentTicketListBinding
    private lateinit var topSheetBehavior: TopSheetBehavior<View>
    private val flightViewModel: FlightViewModel by viewModels()
    private var isRoundTrip : Boolean = false
    private lateinit var depDateTv: String
    private var arrDateTv:String? = null
//    private var singleDepart = "Jakarta"
//    private var singleArrive = "Bali"
//    private var singleDepartDate = "Wed, 30 Nov"
//    private var roundDepart = "Bali"
//    private var roundArrive = "Jakarta"
//    private var roundDepartDate = "Sat, 3 Dec"
//    private var seatClass = "Economy"
    private lateinit var search: RecentSearch
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
        getArgs()
        setToolbar()
        setTopSheetDialog()
        setAdapter()
        setBottomNav()
    }

    private fun getArgs() {
        val bundle = arguments
        if(bundle == null){
            Log.e(TAG, "getArgs: Args Failed")
            return
        }

        val args = TicketListFragmentArgs.fromBundle(bundle)
        search          = args.search
        isRoundTrip     = args.isRoundtrip
        depDateTv       = args.depDateTv // for text in toolbar
        // below will be pass to ticket round list
        arrDateTv       = args.arrDateTv
    }

    private fun setToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)

        // set text base on input user
        binding.toolbarLayout.tvDate.text = Utils.convertDateToDay(depDateTv)
        binding.toolbarLayout.tvFrom.text = search.departCity
        binding.toolbarLayout.tvTo.text = search.arriveCity
        binding.toolbarLayout.tvPassengger.text = search.passengerAmount.toString()
        binding.toolbarLayout.tvSeatClass.text = search.seatClass

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
        flightViewModel.callSearchFlight(search.departDate, search.iataDepartAirport, search.iataArriveAirport)
        adapter.submitList(emptyList())
        flightViewModel.getSearchFlights().observe(viewLifecycleOwner){
            if(it != null){
                adapter.submitList(it.flights)
            }
        }
        binding.rvTicketList.adapter = adapter
        binding.rvTicketList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter.setOnItemClickListener(object : TicketListAdapter.OnItemClickListener{
            override fun onItemClick(view: View, flight: Flight) {
                when(view.id){
                    R.id.btnDetail ->{
                        val detailFragment = TicketDetailFragment(flight)
                        detailFragment.show(requireActivity().supportFragmentManager, detailFragment.tag)
                    }
                    R.id.container ->{
                        Log.d(TAG, "onItemClick: CardView Clicked")
//                        val bundle = Bundle()
                        if(isRoundTrip){
                            val directions = TicketListFragmentDirections.actionTicketListFragmentToTicketRoundTripListFragment(
                                arrDateTv = arrDateTv!!,
                                depFlight = flight,
                                search = search
                            )
                            findNavController().navigate(directions)
//                            bundle.putString(Constants.ARRIVE_DATE, arrDate)
//                            bundle.putString(Constants.ARR_AIRPORT, depAirport)
//                            bundle.putString(Constants.DEP_AIRPORT, arrAirport)
//                            bundle.putString(Constants.ARRIVE_DATE_TV, arrDateTv)
//                            bundle.putString(Constants.DEPART_AIRPORT_CITY, arrCity)
//                            bundle.putString(Constants.ARRIVE_AIRPORT_CITY, depCity)
//                            bundle.putParcelable(Constants.DEP_FLIGHT, flight)
//
//                            findNavController().navigate(R.id.action_ticketListFragment_to_ticketRoundTripListFragment, bundle)
                        }else{
                            val directions = TicketListFragmentDirections.actionTicketListFragmentToFlightConfirmationFragment(flight)
                            findNavController().navigate(directions)
                        }
                    }
                }
            }
        })
    }

    private fun setBottomNav(){
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.isVisible = false
    }
}