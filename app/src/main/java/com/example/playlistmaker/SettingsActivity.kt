package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity: AppCompatActivity() {
    companion object {
        const val NIGHT = "night"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val switchThemes: Switch = findViewById(R.id.switchThemes)
        val imageViewBack: ImageView = findViewById(R.id.imageViewBack)
        val imageViewShare: ImageView = findViewById(R.id.imageViewShare)
        val imageViewSupport: ImageView = findViewById(R.id.imageViewSupport)
        val imageViewArrowNext: ImageView = findViewById(R.id.imageViewArrowNext)

        val sharedPreferences = getSharedPreferences("Mode", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val nightMode = sharedPreferences.getBoolean(NIGHT, false)

        if (nightMode) {
            switchThemes.isChecked = true
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        switchThemes.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                editor.putBoolean(NIGHT,false)
                editor.apply()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor.putBoolean(NIGHT,true)
                editor.apply()
            }
        }


        imageViewBack.setOnClickListener {
            finish()
        }

        imageViewShare.setOnClickListener {
            shareApp()
        }

        imageViewSupport.setOnClickListener {
            contactSupport()
        }

        imageViewArrowNext.setOnClickListener {
            openUserAgreement()
        }
    }

    private fun shareApp() {
        val message = getString(R.string.Hi_look) +
                getString(R.string.practicum_yandex_ru_android_developer)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }

        val chooser = Intent.createChooser(shareIntent, getString(R.string.Share_app))
        startActivity(chooser)
    }

    private fun contactSupport() {
        val recipientEmail = getString(R.string.My_mail)
        val subject = getString(R.string.send_developer)
        val body = getString(R.string.senks)

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        startActivity(emailIntent)

    }

    private fun openUserAgreement() {
        val url = getString(R.string.practicum_yandex_ru_offerta)

        val browserIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        startActivity(browserIntent)

    }
}