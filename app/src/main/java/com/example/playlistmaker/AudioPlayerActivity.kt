package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AudioPlayerActivity: AppCompatActivity() {

    private lateinit var songPictureImageView: ImageView

    private lateinit var trackNameTextView: TextView
    private lateinit var artistNameTextView: TextView
    private lateinit var durationDownloadTextView: TextView
    private lateinit var albumDownloadTextView: TextView
    private lateinit var yearDownloadTextView: TextView
    private lateinit var genreDownloadTextView: TextView
    private lateinit var countryDownloadTextView: TextView

    private lateinit var finishActivity: ImageView
    private lateinit var addButton: ImageView
    private lateinit var playButton: ImageView
    private lateinit var likeButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.audioPlayer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        songPictureImageView = findViewById(R.id.imageViewSongPicture)
        durationDownloadTextView = findViewById(R.id.textViewDurationDownload)
        albumDownloadTextView = findViewById(R.id.textViewAlbumDownload)
        yearDownloadTextView = findViewById(R.id.textViewYearDownload)
        genreDownloadTextView = findViewById(R.id.textViewGenreDownload)
        countryDownloadTextView = findViewById(R.id.textViewCountryDownload)
        trackNameTextView = findViewById(R.id.textViewTrackName)
        artistNameTextView = findViewById(R.id.textViewArtistName)

        addButton = findViewById(R.id.addButton)
        playButton = findViewById(R.id.playButton)
        likeButton = findViewById(R.id.likeButton)

        setUpView()

        finishActivity = findViewById(R.id.imageViewBack)
        finishActivity.setOnClickListener {
            finish()
        }
    }

    private fun setUpView() {
        val jsonTrack = intent.getStringExtra("SELECTED_TRACK")
        if (jsonTrack != null) {
            val track: Track = Gson().fromJson(jsonTrack, Track::class.java)

            Glide.with(applicationContext)
                .load(track.getCoverArtwork())
                .centerCrop()
                .transform(RoundedCorners(25))
                .placeholder(R.drawable.placeholder)
                .into(songPictureImageView)

            trackNameTextView.text = track.trackName
            artistNameTextView.text = track.artistName
            durationDownloadTextView.text = track.trackTime
            albumDownloadTextView.text = track.collectionName

            setUpYearTextView(track)
            genreDownloadTextView.text = track.primaryGenreName
            countryDownloadTextView.text = track.country
        }
    }

    private fun setUpYearTextView(track: Track) {
        val releaseDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        releaseDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val releaseDate: Date? = releaseDateFormat.parse(track.releaseDate)
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        yearDownloadTextView.text = releaseDate?.let { yearFormat.format(it) }
    }
}