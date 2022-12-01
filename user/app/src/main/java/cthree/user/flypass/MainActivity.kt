package cthree.user.flypass

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.androidbolts.topsheet.TopSheetBehavior
import cthree.user.flypass.databinding.ActivityMainBinding


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var topSheetBehavior: TopSheetBehavior<View>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_ios_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        topSheetBehavior = TopSheetBehavior.from(binding.topSheetContainer.topSheet)
        topSheetBehavior.state = TopSheetBehavior.STATE_COLLAPSED
        binding.toolbarLayout.ivDropDown.setOnClickListener {
            if(binding.toolbarLayout.ivDropDown.isChecked)
                topSheetBehavior.state = TopSheetBehavior.STATE_EXPANDED
            else
                topSheetBehavior.state = TopSheetBehavior.STATE_COLLAPSED

        }

    }
}