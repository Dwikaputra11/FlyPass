package cthree.admin.flypass.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.admin.flypass.R
import cthree.admin.flypass.adapter.UserAccountAdapter
import cthree.admin.flypass.databinding.DialogProgressBarBinding
import cthree.admin.flypass.databinding.FragmentUserAccountBinding
import cthree.admin.flypass.utils.SessionManager
import cthree.admin.flypass.viewmodels.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "UserAccountFragment"

@AndroidEntryPoint
class UserAccountFragment : Fragment() {

    lateinit var binding : FragmentUserAccountBinding
    private val adminVM: AdminViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private lateinit var userAccountAdapter : UserAccountAdapter
    private lateinit var progressAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var progressAlertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(requireContext())
        progressAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserAccountBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProgressDialog()
        progressAlertDialog.show()

        val token = sessionManager.getToken()
        Log.d(TAG, "onViewCreated Token: $token ")

        adminVM.callApiUser("Bearer ${token!!.trim()}")
        adminVM.getLiveDataUsers().observe(viewLifecycleOwner) {
            if (it != null){
                binding.rvListUser.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                userAccountAdapter = UserAccountAdapter(it.users)
                binding.rvListUser.adapter = userAccountAdapter
                progressAlertDialog.dismiss()
            }
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