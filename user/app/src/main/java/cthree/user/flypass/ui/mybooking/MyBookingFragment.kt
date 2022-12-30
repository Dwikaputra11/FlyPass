package cthree.user.flypass.ui.mybooking

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayoutMediator
import cthree.user.flypass.R
import cthree.user.flypass.adapter.MyBookingPageAdapter
import cthree.user.flypass.databinding.FragmentMyBookingBinding
import cthree.user.flypass.viewmodels.PreferencesViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MyBookingFragment"

@AndroidEntryPoint
class MyBookingFragment : Fragment(),MenuProvider {

    private lateinit var binding: FragmentMyBookingBinding
    private val prefVM: PreferencesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyBookingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setBottomNav()
        setupToolbar()
        val adapter = MyBookingPageAdapter(requireActivity())
        adapter.addFragment(SearchFragment(), "Search")
        adapter.addFragment(HistoryFragment(), "History")
        adapter.addFragment(RefundFragment(), "Refund")

        binding.viewPager.adapter = adapter
        binding.viewPager.currentItem = 1
        TabLayoutMediator(binding.tabLayout, binding.viewPager){ tab, pos ->
            tab.text =  adapter.getTabTitle(pos)
        }.attach()

        binding.toolbarLayout.toolbar.elevation = 0F
    }
    private fun setBottomNav(){
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.isVisible = true
    }

    private fun setupToolbar() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setLogo(R.drawable.logo_toolbar)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.home_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        Log.d(TAG, "onMenuItemSelected: ")
        return when (menuItem.itemId){
            R.id.notification ->{
                Log.d(TAG, "onMenuItemSelected: Clicked")
                checkUserMember()
                true
            }
            else -> false
        }
    }

    private fun checkUserMember() {
        prefVM.dataUser.observe(viewLifecycleOwner) {
            if (it.token.isNotEmpty()) {
                findNavController().navigate(R.id.action_myBookingFragment_to_notificationFragment)
            } else {
                findNavController().navigate(R.id.action_myBookingFragment_to_notificationFragment)
                // unregistered user
//                DialogCaller(requireActivity())
            }
        }
    }
}