package com.example.gallery.base

import android.app.Application
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

abstract class BaseApplication: Application(), ViewModelStoreOwner {
    private lateinit var viewModelStore: ViewModelStore

    override fun getViewModelStore() = viewModelStore
    override fun onCreate() {
        super.onCreate()
        viewModelStore = ViewModelStore()
    }
}