package cthree.user.flypass.ui.booking

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.user.flypass.R
import cthree.user.flypass.adapter.TravelerDetailsAdapter
import cthree.user.flypass.data.Contact
import cthree.user.flypass.data.Traveler
import cthree.user.flypass.databinding.DialogTwoButtonAlertBinding
import cthree.user.flypass.databinding.FragmentPaymentBinding
import cthree.user.flypass.models.flight.Flight
import cthree.user.flypass.utils.SessionManager
import cthree.user.flypass.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "PaymentFragment"

@AndroidEntryPoint
class PaymentFragment : Fragment() {

    private lateinit var binding                : FragmentPaymentBinding
    private lateinit var depFlight              : Flight
    private var arrFlight                       : Flight? = null
    private lateinit var bookingCode            : String
    private lateinit var paymentMethodFragment  : PaymentMethodFragment
    private lateinit var passengerList          : List<Traveler>
    private lateinit var contactData            : Contact
    private lateinit var sessionManager         : SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        paymentMethodFragment   = PaymentMethodFragment()
        sessionManager          = SessionManager(requireContext())
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getArgs()
        setupToolbar()
        setViews()
        binding.btnPayment.setOnClickListener {
//            notEnoughBalanceDialog()
            if(binding.paymentDetails.tvPaymentMethod.text == "FlightPay"){
                findNavController().navigate(R.id.action_paymentFragment_to_bookingCompleteFragment)
            }else{
                findNavController().navigate(R.id.action_paymentFragment_to_transferBankConfirmFragment)
            }
        }
    }

    private fun setViews() {
        // change visibility if booking is round trip
        binding.roundFlightDetails.root.isVisible   = arrFlight != null
        binding.tvFlypassCode.text                  = bookingCode

        val adapter                         = TravelerDetailsAdapter(sessionManager.getPassenger())
        binding.rvPassenger.adapter         = adapter
        binding.rvPassenger.layoutManager   = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter.submitList(passengerList)

        binding.tvContactEmail.text         = contactData.email
        binding.tvContactNumber.text        = contactData.phoneNumber
        binding.tvContactTitle.text         = contactData.title
        binding.tvContactFirstName.text     = contactData.firstName
        binding.tvContactLastName.text      = contactData.lastName

        // set Details Description Flights
        with(binding.flightDetails){
            tvFlightCode.text           = depFlight.flightCode
            tvAirplaneName.text         = depFlight.airline.name
            tvArriveCity.text           = depFlight.arrivalAirport.city
            tvDepartCity.text           = depFlight.departureAirport.city
            tvArrivalDate.text          = Utils.convertDateToDayDate(depFlight.arrivalDate)
            tvDepartDate.text           = Utils.convertDateToDayDate(depFlight.departureDate)
            tvFlightCode.text           = depFlight.flightCode
            tvArriveTime.text           = Utils.formattedTime(depFlight.arrivalTime)
            tvDepartTime.text           = Utils.formattedTime(depFlight.departureTime)
            tvArrivalAirportName.text   = depFlight.arrivalAirport.name
            tvDepartAirportName.text    = depFlight.departureAirport.name
        }

        // set show Details Flight
        with(binding.flightDetails.cbShowMore){
            setOnClickListener {
                binding.flightShowDetails.root.isVisible = isChecked
            }
        }
        with(binding.flightShowDetails){
            tvFlightType.text   = depFlight.airplane.model
            tvBaggage.text      = depFlight.baggage.toString()
        }


        if(arrFlight != null){

            // set Details Description Flights
            with(binding.roundFlightDetails){
                tvFlightCode.text           = arrFlight!!.flightCode
                tvAirplaneName.text         = arrFlight!!.airline.name
                tvArriveCity.text           = arrFlight!!.arrivalAirport.city
                tvDepartCity.text           = arrFlight!!.departureAirport.city
                tvArrivalDate.text          = Utils.convertDateToDayDate(arrFlight!!.arrivalDate)
                tvDepartDate.text           = Utils.convertDateToDayDate(arrFlight!!.departureDate)
                tvFlightCode.text           = arrFlight!!.flightCode
                tvArriveTime.text           = Utils.formattedTime(arrFlight!!.arrivalTime)
                tvDepartTime.text           = Utils.formattedTime(arrFlight!!.departureTime)
                tvArrivalAirportName.text   = arrFlight!!.arrivalAirport.name
                tvDepartAirportName.text    = arrFlight!!.departureAirport.name
            }

            // set show Details Flight
            with(binding.flightDetails.cbShowMore){
                setOnClickListener {
                    val text                                    = if(isChecked) "Show Less" else "Show More"
                    binding.flightShowDetails.root.isVisible    = isChecked
                    binding.flightDetails.cbShowMore.text       = text
                }
            }
            with(binding.roundFlightDetails.cbShowMore){
                setOnClickListener {
                    val text                                        = if(isChecked) "Show Less" else "Show More"
                    binding.roundFlightShowDetails.root.isVisible   = isChecked
                    binding.roundFlightDetails.cbShowMore.text      = text
                }
            }
            with(binding.roundFlightShowDetails){
                tvFlightType.text   = arrFlight!!.airplane.model
                tvBaggage.text      = arrFlight!!.baggage.toString()
            }
        }

        binding.paymentDetails.llPaymentMethod.setOnClickListener {
            paymentMethodFragment.show(requireActivity().supportFragmentManager, paymentMethodFragment.tag)
        }
        paymentMethodFragment.setOnClickListener(object : PaymentMethodFragment.OnClickListener{
            override fun onClick(method: String) {
                binding.paymentDetails.tvPaymentMethod.text = method
            }
        })

        val arrPrice = arrFlight?.price ?: 0
        val totalPrice = depFlight.price + arrPrice
//        binding.tvPrice.text = Utils.formattedMoney(totalPrice)
    }

    private fun notEnoughBalanceDialog(){
        val notEnoughBinding = DialogTwoButtonAlertBinding.inflate(layoutInflater, null, false)
        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())

        materialAlertDialogBuilder.setView(notEnoughBinding.root)

        val materAlertDialog = materialAlertDialogBuilder.create()
        materAlertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        materAlertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        materAlertDialog.show()

        notEnoughBinding.btnYes.setOnClickListener {
            Log.d(TAG, "notEnoughBalanceDialog: Btn TopUp Clicked")
            materAlertDialog.dismiss()
        }
        notEnoughBinding.tvNo.setOnClickListener {
            Log.d(TAG, "notEnoughBalanceDialog: Maybe Later")
            materAlertDialog.dismiss()
        }
    }

    private fun getArgs() {
        val bundle = arguments
        if(bundle == null){
            Log.e(TAG, "onViewCreated: Args Failed")
            return
        }
        val args = PaymentFragmentArgs.fromBundle(bundle)
        depFlight = args.depFlight
        arrFlight = args.arrFlight
        bookingCode = args.flyPassCode
        contactData = args.contactData
        passengerList = args.passengerList.toList()
        Log.d(TAG, "getArgs: Flight ${args.depFlight} Code $bookingCode")
    }

    private fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.title = "Payment"
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }
    }
}