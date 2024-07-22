package com.example.playlistmaker

class TracksResponse(
    val resultCount: Int,
    val results: List<TrackResponse>)

data class TrackResponse(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String
)