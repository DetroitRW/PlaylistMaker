package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    private val historyKey = "search_history"
    private val gson = Gson()
    private val maxTracksShow = 10

    fun addTrack(track: Track) {
        val tracks = getTracks().toMutableList()
        val existingTrackIndex = tracks.indexOfFirst { it.trackId == track.trackId }

        if (existingTrackIndex != -1) {
            tracks.removeAt(existingTrackIndex)
        }

        tracks.add(0, track)
        if (tracks.size > maxTracksShow) {
            tracks.removeAt(tracks.size - 1)
        }

        saveTracks(tracks)
    }

    fun getTracks(): List<Track> {
        val json = sharedPreferences.getString(historyKey, null)
        return if (json != null) {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun clearHistory() {
        sharedPreferences.edit().remove(historyKey).apply()
    }

    private fun saveTracks(tracks: List<Track>) {
        val json = gson.toJson(tracks)
        sharedPreferences.edit().putString(historyKey, json).apply()
    }
}
