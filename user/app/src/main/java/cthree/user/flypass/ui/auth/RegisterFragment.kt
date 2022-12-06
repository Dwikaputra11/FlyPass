package cthree.user.flypass.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.user.flypass.R
import cthree.user.flypass.databinding.DialogOneButtonAlertBinding
import cthree.user.flypass.databinding.DialogProgressBarBinding
import cthree.user.flypass.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "RegisterFragment"

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var progressAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var errorMsgAlertBuilder: MaterialAlertDialogBuilder
    private lateinit var progressAlertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        errorMsgAlertBuilder = MaterialAlertDialogBuilder(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initProgressDialog()
        binding.btnRegister.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_registerFragment_to_accountInformationFragment)
        }
        binding.tvtoLogin.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun errorMessageDialog(title: String, subtitle: String, btnMsg: String){
        val errorMessageDialog = DialogOneButtonAlertBinding.inflate(layoutInflater, null, false)

        errorMsgAlertBuilder.setView(errorMessageDialog.root)

        val materAlertDialog = errorMsgAlertBuilder.create()
        materAlertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        materAlertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        errorMessageDialog.tvTitle.text = title
        errorMessageDialog.tvSubtitle.text = subtitle
        errorMessageDialog.btnYes.text = btnMsg

        materAlertDialog.show()
        errorMessageDialog.btnYes.setOnClickListener {
            Log.d(TAG, "errorMessageDialog: Clicked")
            materAlertDialog.dismiss()
        }
    }

    private fun initProgressDialog(){
        val progressBarBinding = DialogProgressBarBinding.inflate(layoutInflater, null, false)
        progressAlertDialogBuilder.setView(progressBarBinding.root)

        progressAlertDialog = progressAlertDialogBuilder.create()
        progressAlertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        progressAlertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
    }
}