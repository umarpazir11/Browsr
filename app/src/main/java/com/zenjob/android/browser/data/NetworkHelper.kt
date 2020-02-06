package com.zenjob.android.browser.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.zenjob.android.browser.BuildConfig
import com.zenjob.android.browser.data.repository.TMDBApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import java.util.logging.Level

const val BASE_URL = "https://api.themoviedb.org/3/"

const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w342"


const val FIRST_PAGE = 1
const val POST_PER_PAGE = 20

object NetworkHelper {
    val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(DateJsonAdapter())
            .build()
    }

    val tmdbApiInterceptor: Interceptor by lazy {
        Interceptor { chain ->

            val original = chain.request()
            val originalHttpUrl = original.url()

            val url = originalHttpUrl.newBuilder()
                .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                .build()

            val reqBuilder = original.newBuilder()
                .url(url)
            chain.proceed(reqBuilder.build())
        }
    }




    fun okHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(tmdbApiInterceptor)
            .build()
    }

    fun retrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient())
            .build()
    }

    fun tmdbApi(): TMDBApi {
        return retrofit().create(TMDBApi::class.java)
    }

}
