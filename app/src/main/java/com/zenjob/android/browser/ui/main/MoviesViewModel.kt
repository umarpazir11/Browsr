package com.zenjob.android.browser.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.zenjob.android.browser.data.Movie
import com.zenjob.android.browser.data.repository.NetworkState
import com.zenjob.android.browser.ui.main.MoviePagedListRepository
import io.reactivex.disposables.CompositeDisposable

class MoviesViewModel(private val movieRepository : MoviePagedListRepository) : ViewModel(){

    private val compositeDisposable = CompositeDisposable()

    val  moviePagedList : LiveData<PagedList<Movie>> by lazy {
        movieRepository.fetchLiveMoviePagedList(compositeDisposable)
    }

    val  networkState : LiveData<NetworkState> by lazy {
        movieRepository.getNetworkState()
    }

    fun listIsEmpty(): Boolean {
        return moviePagedList.value?.isEmpty() ?: true
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun refreshData() {
        movieRepository.fetchLiveMoviePagedList(compositeDisposable)
    }



}