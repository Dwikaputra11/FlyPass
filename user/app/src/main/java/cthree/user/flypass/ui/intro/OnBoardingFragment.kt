package cthree.user.flypass.ui.intro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import cthree.user.flypass.R
import cthree.user.flypass.adapter.OnBoardingAdapter
import cthree.user.flypass.data.DummyData
import cthree.user.flypass.data.OnBoarding
import cthree.user.flypass.databinding.FragmentOnBoardingBinding


class OnBoardingFragment : Fragment() {

    private lateinit var binding: FragmentOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnBoardingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setBottomNav()
        binding.vpOnBoard.adapter = OnBoardingAdapter(DummyData.onBoardingItem)
        binding.vpOnBoard.offscreenPageLimit = DummyData.onBoardingItem.size
        binding.vpOnBoard.orientation =ViewPager2.ORIENTATION_HORIZONTAL
        binding.wormDot.attachTo(binding.vpOnBoard)
    }

    private fun setBottomNav(){
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.isVisible = false
    }
}