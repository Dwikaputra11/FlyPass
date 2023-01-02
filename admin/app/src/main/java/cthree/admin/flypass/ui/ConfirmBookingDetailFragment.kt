package cthree.admin.flypass.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import cthree.admin.flypass.R
import cthree.admin.flypass.databinding.FragmentConfirmBookingDetailBinding
import cthree.admin.flypass.utils.SessionManager
import cthree.admin.flypass.viewmodels.ConfirmBookingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmBookingDetailFragment : Fragment() {

    private lateinit var binding: FragmentConfirmBookingDetailBinding
    private val confirmVM: ConfirmBookingViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private var idTransaction = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConfirmBookingDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupToolbar()
        setArgs()
        val token = sessionManager.getToken()

        confirmVM.getConfirmStatus().observe(viewLifecycleOwner){
            if(it != null){
                Toast.makeText(requireContext(), "Confirmation/Reject Success", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }

        binding.btnConfirm.setOnClickListener {
            token?.let {
                confirmVM.confirmBooking(idTransaction, it)
            }
        }

        binding.btnReject.setOnClickListener {
            token?.let {
                confirmVM.rejectBooking(idTransaction, it)
            }
        }
    }

    private fun setArgs(){
        val bundle = arguments ?: return

        val args = ConfirmBookingDetailFragmentArgs.fromBundle(bundle)
        idTransaction = args.idTransaction
    }

    private fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.title = "Confirm Booking Detail"
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }
    }

}