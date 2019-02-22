package com.ss.entertainment.viewmodel

import android.arch.lifecycle.LiveData
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.ss.entertainment.model.Season
import com.ss.entertainment.repository.ApiService
import com.ss.entertainment.repository.RepositoryImpl

class ListViewModel(application: Application) : AndroidViewModel(application) {

    private val listObservable: LiveData<List<Season>>

    private val apiService by lazy {
        ApiService.create()
    }

    init {
        listObservable = RepositoryImpl(apiService).getSeasonsCall()
    }

    fun getListObservable(): LiveData<List<Season>> {
        return listObservable
    }
}