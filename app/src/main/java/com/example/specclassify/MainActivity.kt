package com.example.specclassify

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.example.specclassify.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        
        navView.setOnItemSelectedListener { item ->
            val navOptions = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setRestoreState(true)     
                .setPopUpTo(navController.graph.startDestinationId, false)
                .setEnterAnim(R.anim.fade_in)
                .setExitAnim(R.anim.fade_out)
                .setPopEnterAnim(R.anim.fade_in)
                .setPopExitAnim(R.anim.fade_out)
                .build()

            try {
                navController.navigate(item.itemId, null, navOptions)
                true
            } catch (e: IllegalArgumentException) {
                true
            }
        }
    }
}
