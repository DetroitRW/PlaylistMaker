package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val switchThemes: Button = findViewById(R.id.switchThemes)
        val imageViewBack: ImageView = findViewById(R.id.imageViewBack)
        val imageViewShare: ImageView = findViewById(R.id.imageViewShare)
        val imageViewSupport: ImageView = findViewById(R.id.imageViewSupport)
        val imageViewArrowNext: ImageView = findViewById(R.id.imageViewArrowNext)

        switchThemes.setOnClickListener {

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