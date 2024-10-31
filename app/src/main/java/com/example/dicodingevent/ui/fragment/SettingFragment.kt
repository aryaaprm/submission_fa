package com.example.dicodingevent.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.dicodingevent.R
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingFragment : Fragment() {

    private lateinit var themeSwitch: SwitchMaterial

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        themeSwitch = view.findViewById(R.id.switch_theme)

        //get the current theme setting
        val sharedPreferences = requireActivity().getSharedPreferences("app_preferences", 0)
        val isDarkMode = sharedPreferences.getBoolean("theme_setting", false)

        themeSwitch.isChecked = isDarkMode

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            applyTheme(isChecked)

            sharedPreferences.edit().putBoolean("theme_setting", isChecked).apply()
        }

        return view
    }

    private fun applyTheme(isDarkMode: Boolean) {
        val mode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }
}
