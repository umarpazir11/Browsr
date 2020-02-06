package com.zenjob.android.browser.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.zenjob.android.browser.data.FIRST_PAGE
import com.zenjob.android.browser.data.Movie
import com.zenjob.android.browser.data.NetworkHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MoviesDataSource(
    private val apiService: TMDBApi,
    private val compositeDisposable: CompositeDisposable
) : PageKeyedDataSource<Int, Movie>() {

    private var page = FIRST_PAGE
    val networkState: MutableLiveData<NetworkState> = MutableLiveData()


    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Movie>
    ) {
        compositeDisposable.add(
            apiService
                .getPopularTvShows("de", page)
                .subscribeOn(Schedulers.io())
                .subscribe({ paginatedList ->
                    callback.onResult(paginatedList.results, null, page + 1)
                    networkState.postValue(NetworkState.LOADED)

                }, { throwable ->
                    throwable.printStackTrace()
                    networkState.postValue(NetworkState.ERROR)
                    Log.e("MovieDataSource", throwable.message)
                })
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {

        compositeDisposable.add(
            apiService
                .getPopularTvShows("de", params.key)
                .subscribeOn(Schedulers.io())
                .subscribe({ paginatedList ->
                    if (paginatedList.totalPages >= params.key) {

                        callback.onResult(paginatedList.results, params.key + 1)
                        networkState.postValue(NetworkState.LOADED)
                    } else {
                        networkState.postValue(NetworkState.ENDOFLIST)

                    }
                }, { throwable ->
                    throwable.printStackTrace()
                    networkState.postValue(NetworkState.ERROR)
                    Log.e("MovieDataSource", throwable.message)
                })
        )

    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
    }
}


