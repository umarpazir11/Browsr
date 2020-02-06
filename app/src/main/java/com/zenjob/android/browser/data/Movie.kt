package com.zenjob.android.browser.data

import com.squareup.moshi.Json
import java.util.*

data class Movie(
    val id: Long,
    val imdbId: String?,
    val overview: String?,
    val title: String,
    @Json(name="poster_path")
    val posterPath: String,
    @Json(name = "release_date") val releaseDate: Date?,
    @Json(name = "vote_average") val voteAverage: Float?
) {
    fun getDateFormatted(): String
    {
       return android.text.format.DateFormat.format("yyyy", releaseDate).toString()
    }
}