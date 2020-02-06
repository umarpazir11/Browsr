package com.zenjob.android.browser.data.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.zenjob.android.browser.data.Movie
import com.zenjob.android.browser.data.NetworkHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MoviesRepository(val app: Application) {

    val movieDetail = MutableLiveData<Movie>()

    fun fetchMovie(movieId: Long) {
        NetworkHelper.tmdbApi().getDetails(movieId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ movie ->
                movieDetail.postValue(movie)
            }, { throwable -> throwable.printStackTrace()})
    }
}