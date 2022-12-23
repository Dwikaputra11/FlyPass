package cthree.admin.flypass.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import cthree.admin.flypass.R
import cthree.admin.flypass.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    lateinit var binding : FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnUserAccount.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_userAccountFragment)
        }

        binding.btnAddAdmin.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_registerAdminFragment)
        }

        binding.btnTicketList.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_ticketListFragment)
        }

        binding.btnAddTicket.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addTicketFragment)
        }
    }
}