package cthree.user.flypass.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import cthree.user.flypass.R
import cthree.user.flypass.databinding.FragmentLoginBinding
import cthree.user.flypass.viewmodels.UserViewModel

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var userVM: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        userVM = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        binding.btnLogin.setOnClickListener {
            var email = binding.loginEmail.text.toString()
            var password = binding.loginPassword.text.toString()

            if(email.isEmpty() || password.isEmpty()) {
                binding.loginEmail.error = "Field Masih Kosong"
                binding.loginPassword.error = "Field Masih Kosong"
            }else{
                userVM.callApiUser()
                userVM.getLiveDataUser().observe(viewLifecycleOwner) {
                    if (it != null) {
                        userVM.saveDataId(it.profile.id)
                        Toast.makeText(requireActivity(), "Login Bergasil", Toast.LENGTH_SHORT).show()
                        //            Navigation.findNavController(binding.root).popBackStack(R.id.loginFragment, false)
                        Navigation.findNavController(binding.root).navigate(R.id.action_loginFragment_to_homeFragment)
                    }
                }
            }
        }

        binding.tvtoRegister.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }
}