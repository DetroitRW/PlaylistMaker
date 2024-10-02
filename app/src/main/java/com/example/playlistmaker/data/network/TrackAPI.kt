package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.TracksSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface  TrackAPI {
    @GET("/search?entity=song")
    fun findTrack(@Query("term") expression: String): Call<TracksSearchResponse>
}