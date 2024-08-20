package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
    private lateinit var trackTimeTextView: TextView

    private lateinit var finishActivity: ImageView
    private lateinit var addButton: ImageView
    private lateinit var playButton: ImageView
    private lateinit var likeButton: ImageView

    private lateinit var trackUrl: String

    private var mediaPlayer = MediaPlayer()

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val DELAY = 1000L
        private const val TRACK_TIME = 30L
    }

    private var elapsedTime: Long = 0L

    private var mainThreadHandler: Handler? = null

    private var playerState = STATE_DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.audioPlayer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mainThreadHandler = Handler(Looper.getMainLooper())

        songPictureImageView = findViewById(R.id.imageViewSongPicture)
        durationDownloadTextView = findViewById(R.id.textViewDurationDownload)
        albumDownloadTextView = findViewById(R.id.textViewAlbumDownload)
        yearDownloadTextView = findViewById(R.id.textViewYearDownload)
        genreDownloadTextView = findViewById(R.id.textViewGenreDownload)
        countryDownloadTextView = findViewById(R.id.textViewCountryDownload)
        trackNameTextView = findViewById(R.id.textViewTrackName)
        artistNameTextView = findViewById(R.id.textViewArtistName)
        trackTimeTextView = findViewById(R.id.textViewTrackTime)

        addButton = findViewById(R.id.addButton)
        playButton = findViewById(R.id.playButton)
        likeButton = findViewById(R.id.likeButton)

        setUpView()

        finishActivity = findViewById(R.id.imageViewBack)
        finishActivity.setOnClickListener {
            finish()
        }

        preparePlayer()

        playButton.setOnClickListener {
            playbackControl()
        }
    }

    private fun startTimer(duration: Long) {
        val startTime = System.currentTimeMillis() - elapsedTime
        mainThreadHandler?.post(createUpdateTimerTask(startTime, duration * DELAY))
    }

    private fun createUpdateTimerTask(startTime: Long, duration: Long): Runnable {
        return object : Runnable {
            override fun run() {
                elapsedTime = System.currentTimeMillis() - startTime
                val remainingTime = duration - elapsedTime

                if (remainingTime > 0) {
                    val seconds = elapsedTime / DELAY
                    trackTimeTextView?.text = String.format("%d:%02d", seconds / 60, seconds % 60)
                    mainThreadHandler?.postDelayed(this, DELAY)

                } else {
                    trackTimeTextView?.text = "0:00"
                    playButton.setImageResource(R.drawable.play_button)
                    playButton?.isEnabled = true
                    elapsedTime = 0L
                }
            }
        }
    }

    private fun pauseTimer() {
        mainThreadHandler?.removeCallbacksAndMessages(null) // Stop the timer
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.setImageResource(R.drawable.play_button)
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.pause_button)
        playerState = STATE_PLAYING
        startTimer(TRACK_TIME)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.play_button)
        playerState = STATE_PAUSED
        pauseTimer()
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
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

            trackUrl = track.previewUrl!!
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