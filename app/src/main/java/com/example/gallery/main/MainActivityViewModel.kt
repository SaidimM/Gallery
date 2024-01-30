package com.example.gallery.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {
    private val _isPermissionGranted: MutableLiveData<Boolean> = MutableLiveData(false)
    val isPermissionGranted: LiveData<Boolean> = _isPermissionGranted

    fun permissionGranted(isGranted: Boolean) {
        _isPermissionGranted.postValue(isGranted)
    }
}