package cthree.user.flypass.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.user.flypass.R
import cthree.user.flypass.databinding.DialogOneButtonAlertBinding
import cthree.user.flypass.databinding.DialogProgressBarBinding
import cthree.user.flypass.databinding.FragmentRegisterBinding
import cthree.user.flypass.ui.dialog.DialogCaller
import cthree.user.flypass.utils.AlertButton
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "RegisterFragment"

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding                    : FragmentRegisterBinding
    private lateinit var errorMsgAlertBuilder       : MaterialAlertDialogBuilder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        errorMsgAlertBuilder            = MaterialAlertDialogBuilder(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setBottomNav()
        binding.btnNext.setOnClickListener {
            if(isValid()){
                val direction = RegisterFragmentDirections.actionRegisterFragmentToAccountInformationFragment(
                    email = binding.registerEmail.text.toString(),
                    password = binding.registerPassword.text.toString(),
                    confPassword = binding.registerPassword.text.toString()
                )
                findNavController().navigate(direction)
            }
        }
        binding.tvtoLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun isValid(): Boolean {
        if(binding.registerEmail.text.toString().isEmpty()&&
            binding.registerConfirm.text.toString().isEmpty() &&
            binding.registerPassword.text.toString().isEmpty()
        ){
            DialogCaller(requireActivity())
                .setTitle(R.string.empty_field_title)
                .setMessage(R.string.empty_field_subtitle)
                .setPrimaryButton(R.string.confirm_one_btn_dialog
                ) { dialog, _ ->
                    run {
                        Log.d(TAG, "PrimaryButton: Clicked")
                        dialog.dismiss()
                    }
                }
                .create(layoutInflater, AlertButton.ONE).show()
            return false
        }else if(binding.registerPassword.text.toString() != binding.registerConfirm.text.toString()){
            DialogCaller(requireActivity())
                .setTitle(R.string.conf_password_not_match_title)
                .setMessage(R.string.conf_password_not_match_subtitle)
                .setPrimaryButton(R.string.confirm_one_btn_dialog
                ) { dialog, _ ->
                    run {
                        Log.d(TAG, "PrimaryButton: Clicked")
                        dialog.dismiss()
                    }
                }
                .create(layoutInflater, AlertButton.ONE).show()
            return false
        }
        return  true
    }

    private fun setBottomNav(){
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.isVisible = true
    }
}