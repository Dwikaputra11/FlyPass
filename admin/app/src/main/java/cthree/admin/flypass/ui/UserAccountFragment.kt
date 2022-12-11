package cthree.admin.flypass.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cthree.admin.flypass.R
import cthree.admin.flypass.adapter.UserAccountAdapter
import cthree.admin.flypass.databinding.FragmentUserAccountBinding
import cthree.admin.flypass.utils.SessionManager
import cthree.admin.flypass.viewmodels.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserAccountFragment : Fragment() {

    lateinit var binding : FragmentUserAccountBinding
    private val adminVM: AdminViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private lateinit var userAccountAdapter : UserAccountAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(requireContext())
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

        val token = sessionManager.getToken().toString()

        adminVM.callApiUser(token)
        adminVM.getLiveDataUsers().observe(viewLifecycleOwner) {
            if (it != null){
                binding.rvListUser.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                userAccountAdapter = UserAccountAdapter(it.users)
                binding.rvListUser.adapter = userAccountAdapter
            }
        }
    }
}