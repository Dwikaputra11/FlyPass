package cthree.user.flypass.ui.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cthree.user.flypass.R
import cthree.user.flypass.adapter.NotificationAdapter
import cthree.user.flypass.databinding.FragmentNotificationBinding
import cthree.user.flypass.viewmodels.NotificationViewModel
import cthree.user.flypass.viewmodels.PreferencesViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.net.Socket

@AndroidEntryPoint
class NotificationFragment : Fragment() {

    private lateinit var socket: Socket
    private val notifyVM: NotificationViewModel by viewModels()
    private val prefVM: PreferencesViewModel by viewModels()
    private lateinit var binding: FragmentNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prefVM.dataUser.observe(viewLifecycleOwner){
            if(it.token != null){
                notifyVM.callNotification(it.token)
            }
        }

        val adapter = NotificationAdapter()
        binding.rvNotification.adapter = adapter
        binding.rvNotification.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        notifyVM.getNotification().observe(viewLifecycleOwner){
            if(it != null){
                adapter.submitList(it.notification)
            }
        }
    }

}