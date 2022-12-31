package cthree.user.flypass.ui.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import cthree.user.flypass.R
import cthree.user.flypass.databinding.FragmentNotificationDetailBinding
import cthree.user.flypass.models.notification.Notification
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationDetailFragment : Fragment() {

    private lateinit var binding: FragmentNotificationDetailBinding
    private lateinit var notify: Notification

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setArgs()
        setupToolbar()

        binding.tvTitle.text = notify.bookingCode
        binding.tvMsg.text = notify.message
    }

    private fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.title = "Notification Detail"
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }
    }

    private fun setArgs() {
        val bundle = arguments ?: return

        val args = NotificationDetailFragmentArgs.fromBundle(bundle)
        notify = args.notification
    }

}