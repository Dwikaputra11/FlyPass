package cthree.admin.flypass.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.admin.flypass.R
import cthree.admin.flypass.adapter.UserAccountAdapter
import cthree.admin.flypass.databinding.DialogProgressBarBinding
import cthree.admin.flypass.databinding.FragmentUserAccountBinding
import cthree.admin.flypass.models.user.User
import cthree.admin.flypass.utils.SessionManager
import cthree.admin.flypass.viewmodels.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "UserAccountFragment"

@AndroidEntryPoint
class UserAccountFragment : Fragment() {

    lateinit var binding : FragmentUserAccountBinding
    private val adminVM: AdminViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private val userAccountAdapter : UserAccountAdapter = UserAccountAdapter()
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

        setupToolbar()
        initProgressDialog()
        progressAlertDialog.show()

        setAdapter()
    }

    private fun setAdapter() {
        val token = sessionManager.getToken()
        adminVM.callApiUser("Bearer ${token!!.trim()}")
        userAccountAdapter.submitList(emptyList())
        adminVM.getLiveDataUsers().observe(viewLifecycleOwner) {
            if (it != null){
                userAccountAdapter.submitList(it.users)
            }
        }
        binding.rvListUser.adapter = userAccountAdapter
        binding.rvListUser.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        progressAlertDialog.dismiss()

        userAccountAdapter.setOnItemClickListener(object : UserAccountAdapter.OnItemClickListener{
            override fun onItemClick(user: User) {
                val directions = UserAccountFragmentDirections.actionUserAccountFragmentToUserAccountDetailFragment(user)
                findNavController().navigate(directions)
            }

        })
    }

    private fun initProgressDialog(){
        val progressBarBinding = DialogProgressBarBinding.inflate(layoutInflater, null, false)
        progressAlertDialogBuilder.setView(progressBarBinding.root)

        progressAlertDialog = progressAlertDialogBuilder.create()
        progressAlertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        progressAlertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
    }

    private fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.title = "User Account List"
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_userAccountFragment_to_homeFragment)
        }
    }
}