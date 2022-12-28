package cthree.user.flypass.ui.booking

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import cthree.user.flypass.R
import cthree.user.flypass.databinding.FragmentPaymentMethodBinding
import cthree.user.flypass.utils.Utils
import cthree.user.flypass.viewmodels.FlightPayViewModel
import cthree.user.flypass.viewmodels.PreferencesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentMethodFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentPaymentMethodBinding
    private lateinit var listener: OnClickListener
    private val flightPayVM: FlightPayViewModel by viewModels()
    private val prefVM: PreferencesViewModel by viewModels()

    interface OnClickListener{
        fun onClick(method: String)
    }

    fun setOnClickListener(listener: OnClickListener){
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentMethodBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // check user is member or not
        prefVM.dataUser.observe(viewLifecycleOwner){
            if(it.token.isNotEmpty()){
                flightPayVM.userWallet(it.token)
            }else{
                binding.rlFlightPay.isClickable = false
                binding.rlFlightPay.isFocusable = false
                binding.ivCheckFlightPay.isVisible = false
                binding.tvBalance.text = "Member Only"
            }
        }

        flightPayVM.getUserWallet().observe(viewLifecycleOwner){
            if(it != null){
                binding.tvBalance.text = "Rp ${Utils.formattedMoney(it.wallet.balance)}"
            }
        }


        binding.toolbarLayout.ivClose.setOnClickListener {
            dismiss()
        }
        binding.rlFlightPay.setOnClickListener {
            with(binding.ivCheckFlightPay){
                if(!isVisible){
                    binding.ivCheckTransfer.isVisible = isVisible
                    isVisible = !isVisible
                }
            }
        }
        binding.rlTransferBank.setOnClickListener {
            with(binding.ivCheckTransfer){
                if(!isVisible){
                    binding.ivCheckFlightPay.isVisible = isVisible
                    isVisible = !isVisible
                }
            }
        }
        binding.btnSave.setOnClickListener {
            val method = if(binding.ivCheckTransfer.isVisible) binding.tvTransfer.text else binding.tvFlightPay.text
            listener.onClick(method.toString())
            dismiss()
        }
    }

}