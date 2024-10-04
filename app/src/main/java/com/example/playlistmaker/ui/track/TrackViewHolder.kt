package com.example.playlistmaker.ui.track

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val songPicture: ImageView = itemView.findViewById(R.id.imageViewSongPicture)
    private val trackName: TextView = itemView.findViewById(R.id.textViewTrackName)
    private val artistName: TextView = itemView.findViewById(R.id.textViewArtistName)
    private val trackTime: TextView = itemView.findViewById(R.id.textViewTrackTime)


    fun bind(model: Track) {
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .centerCrop()
            .transform(RoundedCorners(2))
            .placeholder(R.drawable.placeholder)
            .into(songPicture)
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = model.trackTime
    }
}