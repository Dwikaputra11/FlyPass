package cthree.user.flypass

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import cthree.user.flypass.databinding.ActivityMainBinding
import cthree.user.flypass.ui.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        bottomNavigationView.setupWithNavController(navController)

    }

//    override fun onBackPressed() {
//        val fragmentList = supportFragmentManager.fragments
//
//        var handle = false;
//        for(f in fragmentList){
//            if(f is HomeFragment){
//                handle = f.onBackPressed()
//            }
//            if(handle) break
//        }
//        if(!handle){
//            super.onBackPressed()
//        }
//    }
}