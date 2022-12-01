package cthree.user.flypass.ui.intro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import cthree.user.flypass.R
import cthree.user.flypass.databinding.FragmentJoinMemberBinding

class  JoinMemberFragment : Fragment() {

    private lateinit var binding: FragmentJoinMemberBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJoinMemberBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnLogin.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_joinMemberFragment_to_loginFragment)
        }
        binding.btnRegister.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_joinMemberFragment_to_registerFragment)
        }
        binding.btnNanti.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_joinMemberFragment_to_homeFragment)
        }
    }
}