package cthree.user.flypass.ui.flightpay

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import cthree.user.flypass.R
import cthree.user.flypass.databinding.FragmentTopUpSuccessBinding
import cthree.user.flypass.utils.Utils

class TopUpSuccessFragment : Fragment() {

    private lateinit var binding: FragmentTopUpSuccessBinding
    private lateinit var nominal: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopUpSuccessBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getArgs()

        binding.tvNominal.text = Utils.formattedMoney(nominal.toInt())
        binding.tvDate.text = Utils.getDateNow()
        binding.btnFlightPay.setOnClickListener {
            findNavController().navigate(R.id.action_topUpSuccessFragment_to_flightPayFragment)
        }
        binding.btnHome.setOnClickListener {
            findNavController().navigate(R.id.action_topUpSuccessFragment_to_homeFragment)
        }
    }

    private fun getArgs() {
        val bundle = arguments ?: return

        val args = TopUpSuccessFragmentArgs.fromBundle(bundle)
        nominal = args.nominal
    }

}