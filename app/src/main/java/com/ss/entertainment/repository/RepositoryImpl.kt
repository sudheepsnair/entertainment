package com.ss.entertainment.repository

import android.arch.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Response
import android.arch.lifecycle.LiveData
import com.ss.entertainment.model.Season
import com.ss.entertainment.model.VideoResponse
import retrofit2.Callback

class RepositoryImpl(
    private val apiService: ApiService
) {
    val networkState = MutableLiveData<NetworkState>()

    fun getSeasonsCall(): LiveData<List<Season>> {
        val data = MutableLiveData<List<Season>>()
        networkState.postValue(NetworkState.LOADING)
        apiService.getVideos().enqueue(object : Callback<VideoResponse> {
            override fun onResponse(call: Call<VideoResponse>, response: Response<VideoResponse>) {
                if (response.isSuccessful) {
                    val listResponse: VideoResponse? = response.body()
                    val sesonList: List<Season>? = listResponse?.seasons
                    val items = sesonList ?: emptyList()
                    data.value = items
                    networkState.postValue(NetworkState.LOADED)
                } else {
                    networkState.postValue(
                        NetworkState.error("error code: ${response.code()}")
                    )
                }
            }

            override fun onFailure(call: Call<VideoResponse>, t: Throwable) {
                data.value = null
                networkState.postValue(NetworkState.error(t.message ?: "unknown err"))

            }
        })

        return data
    }
}
