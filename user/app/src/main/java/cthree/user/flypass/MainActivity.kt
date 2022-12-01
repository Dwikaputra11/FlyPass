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

    }
}