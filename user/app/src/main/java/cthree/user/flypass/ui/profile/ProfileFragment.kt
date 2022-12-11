package cthree.user.flypass.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import cthree.user.flypass.R
import cthree.user.flypass.databinding.FragmentProfileBinding
import cthree.user.flypass.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "ProfileFragment"

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding    : FragmentProfileBinding
    private val userViewModel       : UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userViewModel.dataUser.observe(viewLifecycleOwner){
            Log.d(TAG, "onViewCreated: ${it.email.isEmpty()}")
            binding.unregistProfileLayout.root.isVisible    = it.email.isEmpty()
            binding.userProfileLayout.root.isVisible        = it.email.isNotEmpty()
            setUserPrefView(it.email.isNotEmpty())
        }
        setViews()
        setBottomNav()
    }

    private fun setViews() {
        binding.cvCallCenter.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_callCenterFragment)
        }
        binding.cvHowToBook.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_howToBookFragment)
        }
        binding.cvFaq.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_faqFragment)
        }
    }

    private fun setUserPrefView(isRegist: Boolean){
        if(isRegist){
            binding.userProfileLayout.cvHistory.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_historyProfileFragment)
            }
            binding.userProfileLayout.cvSeeProfile.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
            }
            binding.userProfileLayout.cvSettings.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
            }
        }else{
            binding.unregistProfileLayout.btnLogin.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
            }
            binding.unregistProfileLayout.btnRegister.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_registerFragment)
            }
        }
    }

    private fun setBottomNav(){
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.isVisible = true
    }
}