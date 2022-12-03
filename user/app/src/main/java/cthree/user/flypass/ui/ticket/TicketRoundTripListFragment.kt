package cthree.user.flypass.ui.ticket

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.arraySetOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidbolts.topsheet.TopSheetBehavior
import cthree.user.flypass.R
import cthree.user.flypass.adapter.TicketListAdapter
import cthree.user.flypass.databinding.FragmentTicketRoundTripListBinding
import cthree.user.flypass.models.flight.Flight
import cthree.user.flypass.utils.Constants
import cthree.user.flypass.viewmodels.FlightViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "TicketRoundTripListFragment"

@AndroidEntryPoint
class TicketRoundTripListFragment : Fragment() {

    private lateinit var binding: FragmentTicketRoundTripListBinding
    private lateinit var topSheetBehavior: TopSheetBehavior<View>
    private val flightViewModel: FlightViewModel by viewModels()
    private var seatClass = "Economy"
    private lateinit var arrDateTv:String
    private lateinit var depCity: String
    private lateinit var arrCity: String
    private lateinit var arrDate: String
    private lateinit var arrAirport: String
    private lateinit var depAirport: String
    private lateinit var depFlight: Flight
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
        depCity         = arguments?.getString(Constants.DEPART_AIRPORT_CITY).toString()
        depAirport      = arguments?.getString(Constants.DEP_AIRPORT).toString()
        arrDate         = arguments?.getString(Constants.ARRIVE_DATE).toString()
        arrAirport      = arguments?.getString(Constants.ARR_AIRPORT).toString()
        arrDateTv       = arguments?.getString(Constants.ARRIVE_DATE_TV).toString()
        arrCity         = arguments?.getString(Constants.ARRIVE_AIRPORT_CITY).toString()
        depFlight       = arguments?.getParcelable<Flight>(Constants.DEP_FLIGHT) as Flight
        getArgs()
        setToolbar()
        setTopSheetDialog()
        setAdapter()
        roundTrip()
    }

    private fun getArgs() {
        val bundle = arguments
        if(bundle == null){
            Log.e(TAG, "getArgs: Args Failed")
            return
        }
        val args = TicketRoundTripListFragmentArgs.fromBundle(bundle)
        depCity         = args.depCity
        depAirport      = args.depAirport
        arrDate         = args.arrDate
        arrAirport      = args.arrAirport
        arrDateTv       = args.arrDateTv
        arrCity         = args.arrCity
        depFlight       = args.depFlight
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
        flightViewModel.callSearchFlight(arrDate, depAirport, arrAirport)
        flightViewModel.getSearchFlights().observe(viewLifecycleOwner){
            if(it != null){
                adapter.submitList(it.flights)
            }
        }
        adapter.setOnItemClickListener(object : TicketListAdapter.OnItemClickListener{
            override fun onItemClick(view: View, flight: Flight) {
                when(view.id){
                    R.id.btnDetail ->{
                        val detailFragment = TicketDetailFragment(flight)
                        detailFragment.show(requireActivity().supportFragmentManager, detailFragment.tag)
                    }
                    R.id.container ->{
                        val bundle = Bundle()
                        // passing flights using bundle to flight confirmation
                        bundle.putParcelable(Constants.DEP_FLIGHT, depFlight)
                        bundle.putParcelable(Constants.ARR_FLIGHT, flight)
                        Navigation.findNavController(binding.root).navigate(R.id.action_ticketRoundTripListFragment_to_flightConfirmationFragment, bundle)
                    }
                }
            }
        })
    }

    private fun roundTrip(){
        binding.toolbarLayout.tvFrom.text           = depCity
        binding.toolbarLayout.tvTo.text             = arrCity
        binding.toolbarLayout.tvDate.text           = arrDateTv
        binding.toolbarLayout.tvSeatClass.text      = seatClass
    }
}