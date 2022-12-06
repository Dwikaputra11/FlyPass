package cthree.user.flypass.ui.booking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import cthree.user.flypass.R
import cthree.user.flypass.databinding.FragmentPaymentMethodBinding

class PaymentMethodFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentPaymentMethodBinding
    private lateinit var listener: OnClickListener

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
            val method = if(binding.ivCheckTransfer.isVisible) binding.tvTransfer.text else binding.tvBalance.text
            listener.onClick(method.toString())
            dismiss()
        }
    }

}