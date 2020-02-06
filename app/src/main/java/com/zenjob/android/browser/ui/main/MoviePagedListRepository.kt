package com.zenjob.android.browser.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.zenjob.android.browser.data.Movie
import com.zenjob.android.browser.data.POST_PER_PAGE
import com.zenjob.android.browser.data.repository.MoviesDataSource
import com.zenjob.android.browser.data.repository.MoviesDataSourceFactory
import com.zenjob.android.browser.data.repository.NetworkState
import com.zenjob.android.browser.data.repository.TMDBApi
import io.reactivex.disposables.CompositeDisposable

class MoviePagedListRepository (private val apiService : TMDBApi) {

    lateinit var moviePagedList: LiveData<PagedList<Movie>>
    lateinit var moviesDataSourceFactory: MoviesDataSourceFactory

    fun fetchLiveMoviePagedList (compositeDisposable: CompositeDisposable) : LiveData<PagedList<Movie>> {
        moviesDataSourceFactory = MoviesDataSourceFactory(apiService,compositeDisposable)

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(POST_PER_PAGE)
            .build()

        moviePagedList = LivePagedListBuilder(moviesDataSourceFactory, config).build()

        return moviePagedList
    }
    fun getNetworkState(): LiveData<NetworkState> {
        return Transformations.switchMap<MoviesDataSource, NetworkState>(
            moviesDataSourceFactory.moviesLiveDataSource, MoviesDataSource::networkState)
    }

}