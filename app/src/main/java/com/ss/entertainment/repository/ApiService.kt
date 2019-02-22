package com.ss.entertainment.repository

import android.util.Log
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

import com.ss.entertainment.model.VideoResponse

interface ApiService {

    @GET("/g-palmer/53eb655b31036d4132d25371a0f8e6ba/raw/af1b686982b2636319cd8980ecc25a79eda64ead/yent-interview.json")
    fun getVideos(): Call<VideoResponse>

    companion object {
        private const val BASE_URL = "https://gist.githubusercontent.com/"
        fun create(): ApiService = create(HttpUrl.parse(BASE_URL)!!)
        private fun create(httpUrl: HttpUrl): ApiService {
            val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                Log.d("API", it)
            })
            logger.level = HttpLoggingInterceptor.Level.BASIC

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}
