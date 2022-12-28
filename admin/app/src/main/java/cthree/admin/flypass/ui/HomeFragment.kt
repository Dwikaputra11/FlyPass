package cthree.admin.flypass.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.work.WorkInfo
import cthree.admin.flypass.R
import cthree.admin.flypass.databinding.FragmentHomeBinding
import cthree.admin.flypass.utils.SessionManager
import cthree.admin.flypass.viewmodels.AirlineViewModel
import cthree.admin.flypass.viewmodels.AirplaneViewModel
import cthree.admin.flypass.viewmodels.AirportViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    lateinit var binding : FragmentHomeBinding
    private lateinit var sessionManager: SessionManager
    private val airportViewModel : AirportViewModel by viewModels()
    private val airlineViewModel : AirlineViewModel by viewModels()
    private val airplaneViewModel : AirplaneViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(requireContext())
    }

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

        if(sessionManager.getIsFirstInstall()){
            airportViewModel.fetchAirportData()
            airlineViewModel.fetchAirlineData()
            airplaneViewModel.fetchAirplaneData()
            airportViewModel.getAirportWorkerInfo().observe(viewLifecycleOwner){
                val workInfo = it[0]
                if(workInfo.state == WorkInfo.State.SUCCEEDED){
                    sessionManager.setIsFirstInstall(false)
                }
            }
            airlineViewModel.getAirlineWorkerInfo().observe(viewLifecycleOwner){
                val workInfo = it[0]
                if(workInfo.state == WorkInfo.State.SUCCEEDED){
                    sessionManager.setIsFirstInstall(false)
                }
            }
            airplaneViewModel.getAirplaneWorkerInfo().observe(viewLifecycleOwner){
                val workInfo = it[0]
                if(workInfo.state == WorkInfo.State.SUCCEEDED){
                    sessionManager.setIsFirstInstall(false)
                }
            }
        }

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