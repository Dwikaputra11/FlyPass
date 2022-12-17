package cthree.user.flypass.ui.mybooking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayoutMediator
import cthree.user.flypass.R
import cthree.user.flypass.adapter.MyBookingPageAdapter
import cthree.user.flypass.databinding.FragmentMyBookingBinding

class MyBookingFragment : Fragment() {

    private lateinit var binding: FragmentMyBookingBinding

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
}