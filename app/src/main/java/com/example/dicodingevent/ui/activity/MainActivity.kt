package com.example.dicodingevent.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.dicodingevent.R
import com.example.dicodingevent.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isDarkModeEnabled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {

        //get saved theme
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        isDarkModeEnabled = sharedPreferences.getBoolean("theme_setting", false)

        applyThemeIfNeeded(isDarkModeEnabled)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //navigation setup
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
    }

    private fun applyThemeIfNeeded(isDarkMode: Boolean) {
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        val targetMode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        if (currentMode != targetMode) {
            AppCompatDelegate.setDefaultNightMode(targetMode)
        }
    }
}
