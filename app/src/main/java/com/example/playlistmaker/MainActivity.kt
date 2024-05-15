package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val buttonSearch: Button = findViewById(R.id.button_search)
        val buttonMediaLibrary: Button = findViewById(R.id.button_media_library)
        val buttonSettings: Button = findViewById(R.id.button_settings)

        buttonSearch.setOnClickListener{
            val intentSearch = Intent(this, SearchActivity::class.java)
            startActivity(intentSearch)
        }

        buttonMediaLibrary.setOnClickListener {
            val intentMedia = Intent(this, MediaLibraryActivity::class.java)
            startActivity(intentMedia)
        }

        buttonSettings.setOnClickListener {
            val intentSettings = Intent(this, SettingsActivity::class.java)
            startActivity(intentSettings)        }
    }
}