package com.example.gallery.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gallery.Constants
import java.io.File

class MainActivityViewModel : ViewModel() {
    private val TAG = "MainActivityViewModel"
    private val _isPermissionGranted: MutableLiveData<Boolean> = MutableLiveData(false)
    val isPermissionGranted: LiveData<Boolean> = _isPermissionGranted

    fun permissionGranted(isGranted: Boolean) {
        _isPermissionGranted.postValue(isGranted)
    }

    fun createDirectories() {
        val albumDir = Constants.ALBUM_COVER_DIR
        val lyricDir = Constants.LYRIC_DIR
        if (File(albumDir).mkdirs()) LogUtil.d(TAG, "create album dir success")
        if (File(lyricDir).mkdirs()) LogUtil.d(TAG, "create lyric dir success")
    }
}