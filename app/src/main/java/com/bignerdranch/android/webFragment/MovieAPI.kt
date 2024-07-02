package com.bignerdranch.android.webFragment

import com.bignerdranch.android.webFragment.ui.models.Movie
import retrofit2.Call
import retrofit2.http.GET

interface MovieAPI {

    @GET("movies")
    fun getData(): Call<List<Movie>>
}