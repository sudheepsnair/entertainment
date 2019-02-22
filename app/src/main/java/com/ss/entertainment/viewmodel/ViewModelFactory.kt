package com.ss.entertainment.viewmodel

import android.arch.lifecycle.ViewModel
import android.app.Application
import android.arch.lifecycle.ViewModelProvider


@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val mApplication: Application) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass == ListViewModel::class.java) {
            ListViewModel(mApplication) as T
        }  else {
            super.create(modelClass)
        }
    }
}
