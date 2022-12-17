package cthree.user.flypass.ui.booking

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import cthree.user.flypass.R
import cthree.user.flypass.databinding.FragmentFilghtConfirmationBinding
import cthree.user.flypass.models.flight.Flight
import cthree.user.flypass.utils.Utils
import cthree.user.flypass.viewmodels.UserViewModel
import cthree.user.flypass.viewmodels.WishlistViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "FlightConfirmationFragment"
@AndroidEntryPoint
class FlightConfirmationFragment : Fragment(), MenuProvider{

    private lateinit var binding: FragmentFilghtConfirmationBinding
    private val wishlistVM: WishlistViewModel by viewModels()
    private lateinit var depFlight: Flight
    private var arrFlight: Flight? = null
    private val userVM: UserViewModel by viewModels()
    private lateinit var userToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilghtConfirmationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupToolbar()
        getArgs()
        setViews()

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.confirmLayout.btnConfirm.setOnClickListener {
            navigateToBooking()
        }
    }

    private fun navigateToBooking() {
        val directions = FlightConfirmationFragmentDirections.actionFlightConfirmationFragmentToBookingFragment(depFlight, arrFlight)
        findNavController().navigate(directions)
    }

    private fun setViews() {
        // change visibility if booking is round trip
        binding.rvArriveTitle.isVisible = arrFlight != null
        binding.roundFlightDesc.root.isVisible= arrFlight != null
        binding.roundFlightDetails.root.isVisible = arrFlight != null

        // set Small Description Flight
        with(binding.flightDesc){
            tvFlightCode.text = depFlight.flightCode
            tvSeatClass.text = "Economy"
            tvFlightDirect.text = "Direct"
            tvArriveCity.text = depFlight.arrivalAirport.city
            tvDepartCity.text = depFlight.departureAirport.city
            tvAirplaneName.text = depFlight.airline.name
        }

        // set Details Description Flights
        with(binding.flightDetails){
            tvFlightCode.text = depFlight.flightCode
            tvAirplaneName.text = depFlight.airline.name
            tvArriveCity.text = depFlight.arrivalAirport.city
            tvDepartCity.text = depFlight.departureAirport.city
            tvArrivalDate.text = Utils.convertDateToDayDate(depFlight.arrivalDate)
            tvDepartDate.text = Utils.convertDateToDayDate(depFlight.departureDate)
            tvFlightCode.text = depFlight.flightCode
            tvArriveTime.text = Utils.formattedTime(depFlight.arrivalTime)
            tvDepartTime.text = Utils.formattedTime(depFlight.departureTime)
            tvArrivalAirportName.text = depFlight.arrivalAirport.name
            tvDepartAirportName.text = depFlight.departureAirport.name
        }

        // set show Details Flight
        with(binding.flightDetails.cbShowMore){
            setOnClickListener {
                binding.flightShowDetails.root.isVisible = isChecked
            }
        }
        with(binding.flightShowDetails){
            tvFlightType.text = depFlight.airplane.model
            tvBaggage.text = depFlight.baggage.toString()
        }


        if(arrFlight != null){
            // set Small Description Flight
            with(binding.roundFlightDesc){
                tvFlightCode.text = arrFlight!!.flightCode
                tvSeatClass.text = "Economy"
                tvFlightDirect.text = "Direct"
                tvArriveCity.text = arrFlight!!.arrivalAirport.city
                tvDepartCity.text = arrFlight!!.departureAirport.city
                tvAirplaneName.text = arrFlight!!.airline.name
            }

            // set Details Description Flights
            with(binding.roundFlightDetails){
                tvFlightCode.text = arrFlight!!.flightCode
                tvAirplaneName.text = arrFlight!!.airline.name
                tvArriveCity.text = arrFlight!!.arrivalAirport.city
                tvDepartCity.text = arrFlight!!.departureAirport.city
                tvArrivalDate.text = Utils.convertDateToDayDate(arrFlight!!.arrivalDate)
                tvDepartDate.text = Utils.convertDateToDayDate(arrFlight!!.departureDate)
                tvFlightCode.text = arrFlight!!.flightCode
                tvArriveTime.text = Utils.formattedTime(arrFlight!!.arrivalTime)
                tvDepartTime.text = Utils.formattedTime(arrFlight!!.departureTime)
                tvArrivalAirportName.text = arrFlight!!.arrivalAirport.name
                tvDepartAirportName.text = arrFlight!!.departureAirport.name
            }

            // set show Details Flight
            with(binding.flightDetails.cbShowMore){
                setOnClickListener {
                    binding.flightShowDetails.root.isVisible = isChecked
                    binding.flightDetails.cbShowMore.text = if(isChecked) "Show Less" else "Show More"
                }
            }
            with(binding.roundFlightDetails.cbShowMore){
                setOnClickListener {
                    binding.roundFlightShowDetails.root.isVisible = isChecked
                    binding.roundFlightDetails.cbShowMore.text = if(isChecked) "Show Less" else "Show More"
                }
            }
            with(binding.roundFlightShowDetails){
                tvFlightType.text = arrFlight!!.airplane.model
                tvBaggage.text = arrFlight!!.baggage.toString()
            }
        }

        val arrPrice = arrFlight?.price ?: 0
        val totalPrice = depFlight.price + arrPrice
        binding.confirmLayout.tvPrice.text = Utils.formattedMoney(totalPrice)

    }



    private fun getArgs(){
        val bundle = arguments
        if(bundle == null){
            Log.e(TAG, "onViewCreated: Args Failed")
            return
        }
        val args = FlightConfirmationFragmentArgs.fromBundle(bundle)
        depFlight = args.depFlight
        arrFlight = args.arrFlight
        Log.d(TAG, "getArgs: Flight ${args.depFlight}")
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

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.add_wishlist_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId){
            R.id.addWishlist ->{
                Log.d(TAG, "onMenuItemSelected: Clicked")
//                wishlistVM.getUserWishlist()
                true
            }
            else -> false
        }
    }
}