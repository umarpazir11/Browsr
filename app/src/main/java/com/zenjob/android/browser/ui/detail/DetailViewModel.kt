package com.zenjob.android.browser.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.zenjob.android.browser.data.repository.MoviesRepository

class DetailViewModel (val app: Application) : AndroidViewModel(app) {

    private val dataRepo =
        MoviesRepository(app)
    val movieDetail = dataRepo.movieDetail

    fun movieDetails(movieId: Long){
        dataRepo.fetchMovie(movieId)
    }

}