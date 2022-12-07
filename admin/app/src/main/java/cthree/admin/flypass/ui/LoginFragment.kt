package cthree.admin.flypass.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import cthree.admin.flypass.R
import cthree.admin.flypass.databinding.FragmentLoginBinding
import cthree.admin.flypass.utils.SessionManager
import cthree.admin.flypass.utils.Utils
import cthree.admin.flypass.viewmodels.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    lateinit var binding : FragmentLoginBinding
    private val adminVM: AdminViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adminVM.getLoginToken().observe(viewLifecycleOwner) {
            if(it != null){
                Log.d("string token", "onViewCreated: $it")
                sessionManager.setToken(it)
                adminVM.saveData(Utils.decodeAccountToken(it))
                Navigation.findNavController(binding.root).navigate(R.id.action_loginFragment_to_homeFragment)
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            if(email.isEmpty() || password.isEmpty()) {
                binding.loginEmail.error = "Field Masih Kosong"
                binding.loginPassword.error = "Field Masih Kosong"
            }else{
                adminVM.loginAdmin(email, password)
                adminVM.saveLoginStatus(true)
//                adminVM.postDataAdmin().observe(viewLifecycleOwner) {
//                    if (it != null) {
//                        Toast.makeText(requireActivity(), "Login Berhasil", Toast.LENGTH_SHORT).show()
//                        Navigation.findNavController(binding.root).navigate(R.id.action_loginFragment_to_homeFragment)
//                    }
//                }
            }
        }
    }
}