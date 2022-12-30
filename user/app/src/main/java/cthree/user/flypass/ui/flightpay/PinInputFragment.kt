package cthree.user.flypass.ui.flightpay

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hanks.passcodeview.PasscodeView.PasscodeViewListener
import com.hanks.passcodeview.PasscodeView.PasscodeViewType
import cthree.user.flypass.R
import cthree.user.flypass.data.InputPinMember
import cthree.user.flypass.databinding.DialogProgressBarBinding
import cthree.user.flypass.databinding.FragmentPinInputBinding
import cthree.user.flypass.ui.dialog.DialogCaller
import cthree.user.flypass.utils.AlertButton
import cthree.user.flypass.utils.InputPinFrom
import cthree.user.flypass.viewmodels.BookingViewModel
import cthree.user.flypass.viewmodels.FlightPayViewModel
import cthree.user.flypass.viewmodels.PreferencesViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "PinInputFragment"
@AndroidEntryPoint
class PinInputFragment(private val inputPinFrom: InputPinFrom) : DialogFragment() {

    private lateinit var binding: FragmentPinInputBinding
    private var pin: String? = null
    private lateinit var progressAlertDialogBuilder : MaterialAlertDialogBuilder
    private lateinit var progressAlertDialog        : AlertDialog
    private val prefVM: PreferencesViewModel by viewModels()
    private val flightPayVM: FlightPayViewModel by viewModels()
    private val bookingVM: BookingViewModel by viewModels()
    private lateinit var userToken: String
    private var bookingId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        setStyle(STYLE_NORMAL,android.R.style.Theme_Light_NoTitleBar_Fullscreen)
    }

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val dialog = dialog
//        if(dialog != null){
//            val width = ViewGroup.LayoutParams.MATCH_PARENT
//            val height = ViewGroup.LayoutParams.MATCH_PARENT
//            requireDialog().window?.setLayout(width, height)
//        }
//        return super.onCreateDialog(savedInstanceState)
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPinInputBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setBottomNav()
        initProgressDialog()
        prefVM.dataUser.observe(viewLifecycleOwner){
            if(it.token.isNotEmpty()){
                userToken = it.token
            }
            if(it.pinMember.isNotEmpty()){
                Log.d(TAG, "Pin Member: ${it.pinMember}")
                pin = it.pinMember
                binding.pinView.localPasscode = it.pinMember
            }
        }
        flightPayVM.getActivateWallet().observe(viewLifecycleOwner){
            if(it != null){
                Log.d(TAG, "Activation Success ")
                progressAlertDialog.dismiss()
                dismiss()
            }
        }
        bookingVM.getBookingBalancePay().observe(viewLifecycleOwner){
            if(it != null){
                progressAlertDialog.dismiss()
                findNavController().navigate(R.id.action_paymentFragment_to_bookingCompleteFragment)
                dismiss()
            }
        }
        binding.pinView.passcodeType =  if(inputPinFrom == InputPinFrom.FLIGHTPAY)
            PasscodeViewType.TYPE_SET_PASSCODE
        else
            PasscodeViewType.TYPE_CHECK_PASSCODE



        binding.pinView.listener = object : PasscodeViewListener{
            override fun onFail() {
            }

            override fun onSuccess(number: String?) {
                if(number != null){
                    if(inputPinFrom == InputPinFrom.PAYMENT){
                        progressAlertDialog.show()
                        bookingVM.paymentWithFlightPay(bookingId, userToken,InputPinMember(number))
                    }else if(inputPinFrom == InputPinFrom.FLIGHTPAY){
                        progressAlertDialog.show()
                        prefVM.savePinMember(number)
                        flightPayVM.activateWallet(InputPinMember(number), userToken)
                    }
                }
            }
        }

        binding.tvForgotPinNumber.setOnClickListener {
            DialogCaller(requireActivity())
                .setTitle(R.string.forgot_pin_title)
                .setMessage(R.string.forgot_pin_msg)
                .setPrimaryButton(R.string.forgot_pin_continue_btn){ dialog, _ ->
                    run{
                        binding.pinView.passcodeType = PasscodeViewType.TYPE_SET_PASSCODE
                        dialog.dismiss()
                    }
                }
                .setSecondaryButton(R.string.forgot_pin_cancel_btn){ dialog, _ ->
                    run{
                        dialog.dismiss()
                    }
                }
                .create(layoutInflater, AlertButton.TWO)
                .show()
        }
    }

    private fun initProgressDialog(){
        val progressBarBinding = DialogProgressBarBinding.inflate(layoutInflater, null, false)
        progressAlertDialogBuilder.setView(progressBarBinding.root)

        progressAlertDialog = progressAlertDialogBuilder.create()
        progressAlertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        progressAlertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
    }

    fun setBookingId(bookingId: Int){
        this.bookingId = bookingId
    }

    private fun setBottomNav(){
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.isVisible = false
    }
}