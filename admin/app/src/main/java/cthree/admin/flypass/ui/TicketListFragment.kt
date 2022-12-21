package cthree.admin.flypass.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.admin.flypass.R
import cthree.admin.flypass.adapter.TicketAdapter
import cthree.admin.flypass.databinding.DialogProgressBarBinding
import cthree.admin.flypass.databinding.FragmentTicketListBinding
import cthree.admin.flypass.models.ticketflight.Flight
import cthree.admin.flypass.utils.SessionManager
import cthree.admin.flypass.viewmodels.TicketViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TicketListFragment : Fragment() {

    private val ticketVM: TicketViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private val ticketAdapter : TicketAdapter = TicketAdapter()
    private lateinit var progressAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var progressAlertDialog: AlertDialog

    lateinit var binding : FragmentTicketListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(requireContext())
        progressAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTicketListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        initProgressDialog()
        progressAlertDialog.show()

        ticketVM.callApiTicket()
        ticketAdapter.submitList(emptyList())
        ticketVM.getLiveDataTicket().observe(viewLifecycleOwner) {
            if (it != null){
                ticketAdapter.submitList(it.flights)
            }
        }
        binding.rvListTicket.adapter = ticketAdapter
        binding.rvListTicket.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        progressAlertDialog.dismiss()

        ticketAdapter.setOnItemClickListener(object : TicketAdapter.OnItemClickListener{
            override fun onItemClick(view: View, flight: Flight) {
                when(view.id){
                    R.id.btnDetail -> {
                        val directions = TicketListFragmentDirections.actionTicketListFragmentToDetailTicketFragment(flight)
                        findNavController().navigate(directions)
                    }
                }
            }

        })
    }

    private fun initProgressDialog(){
        val progressBarBinding = DialogProgressBarBinding.inflate(layoutInflater, null, false)
        progressAlertDialogBuilder.setView(progressBarBinding.root)

        progressAlertDialog = progressAlertDialogBuilder.create()
        progressAlertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        progressAlertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
    }

    private fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.title = "Ticket List"
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }
    }
}