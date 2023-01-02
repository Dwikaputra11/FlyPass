package cthree.admin.flypass.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import cthree.admin.flypass.R
import cthree.admin.flypass.adapter.ConfirmBookingAdapter
import cthree.admin.flypass.databinding.FragmentConfirmBookingBinding
import cthree.admin.flypass.models.transaction.Transaction
import cthree.admin.flypass.utils.SessionManager
import cthree.admin.flypass.viewmodels.ConfirmBookingViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "ConfirmBookingFragment"

@AndroidEntryPoint
class ConfirmBookingFragment : Fragment() {

    private lateinit var binding: FragmentConfirmBookingBinding
    private val confirmVM: ConfirmBookingViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConfirmBookingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupToolbar()
        val adapter = ConfirmBookingAdapter()
        binding.rvRefundStatus.adapter = adapter
        val token = sessionManager.getToken()
        token?.let {
            confirmVM.allTransaction(it)
        }

        confirmVM.getAllTransaction().observe(viewLifecycleOwner){
            if(it != null){
                val confirmList = it.transaction.filter { transaction ->  !transaction.isPayed }
                Log.d(TAG, "onViewCreated: $confirmList")
                adapter.submitList(confirmList)
            }
        }
        adapter.setOnItemClickListener(object : ConfirmBookingAdapter.OnItemClickListener{
            override fun onItemClick(transaction: Transaction) {
                val directions = ConfirmBookingFragmentDirections.actionConfirmBookingFragmentToConfirmBookingDetailFragment(transaction.id)
                findNavController().navigate(directions)
            }
        })
    }

    private fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.title = "Confirm Booking"
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }
    }
}