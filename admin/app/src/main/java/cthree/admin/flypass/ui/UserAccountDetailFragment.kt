package cthree.admin.flypass.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import cthree.admin.flypass.R
import cthree.admin.flypass.databinding.FragmentUserAccountDetailBinding
import cthree.admin.flypass.models.user.User
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserAccountDetailFragment() : Fragment() {

    lateinit var binding : FragmentUserAccountDetailBinding
    private lateinit var detailUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserAccountDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        getArgs()
        setViews()

    }

    private fun setViews() {
        binding.nameUser.setText(detailUser.name)
        binding.genderUser.setText(detailUser.gender)
        binding.birthdateUser.setText(detailUser.birthDate)
        binding.emailUser.setText(detailUser.email)
        binding.phoneUser.setText(detailUser.phone)
        binding.roleUser.setText(detailUser.role.name)
    }

    private fun getArgs() {
        val bundle = arguments
        if(bundle == null){
            return
        }
        val args = UserAccountDetailFragmentArgs.fromBundle(bundle)
        detailUser = args.detailUser
    }

    private fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.title = "User Account Detail"
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }
    }
}