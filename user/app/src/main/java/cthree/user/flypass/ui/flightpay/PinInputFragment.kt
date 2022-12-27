package cthree.user.flypass.ui.flightpay

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hanks.passcodeview.PasscodeView.PasscodeViewListener
import com.hanks.passcodeview.PasscodeView.PasscodeViewType
import cthree.user.flypass.R
import cthree.user.flypass.data.InputPinMember
import cthree.user.flypass.databinding.FragmentPinInputBinding
import cthree.user.flypass.viewmodels.FlightPayViewModel
import cthree.user.flypass.viewmodels.PreferencesViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "PinInputFragment"
@AndroidEntryPoint
class PinInputFragment : DialogFragment() {

    private lateinit var binding: FragmentPinInputBinding
    private var pin: String? = null
    private val prefVM: PreferencesViewModel by viewModels()
    private val flightPayVM: FlightPayViewModel by viewModels()
    private lateinit var userToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        prefVM.dataUser.observe(viewLifecycleOwner){
            if(it.token.isNotEmpty()){
                userToken = it.token
            }
        }
        flightPayVM.getActivateWallet().observe(viewLifecycleOwner){
            if(it != null){
                Log.d(TAG, "Activation Success ")
                dismiss()
            }
        }
        binding.pinView.listener = object : PasscodeViewListener{
            override fun onFail() {
            }

            override fun onSuccess(number: String?) {
                if(number != null){
                    prefVM.savePinMember(number)
                    flightPayVM.activateWallet(InputPinMember(number), userToken)
                }
            }

        }
    }

    private fun setBottomNav(){
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.isVisible = false
    }
}