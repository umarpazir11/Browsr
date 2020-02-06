package com.zenjob.android.browser.data.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.zenjob.android.browser.data.Movie
import io.reactivex.disposables.CompositeDisposable

class MoviesDataSourceFactory(private val apiService : TMDBApi, private val compositeDisposable: CompositeDisposable
) : DataSource.Factory<Int, Movie>() {

    val moviesLiveDataSource =  MutableLiveData<MoviesDataSource>()


    override fun create(): DataSource<Int, Movie> {
        val movieDataSource = MoviesDataSource(apiService,compositeDisposable)

        moviesLiveDataSource.postValue(movieDataSource)
        return movieDataSource
    }


}