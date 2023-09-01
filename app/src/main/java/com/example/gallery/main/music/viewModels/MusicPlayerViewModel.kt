package com.example.gallery.main.music.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gallery.media.MusicRepository
import com.google.android.material.bottomsheet.BottomSheetBehavior

class MusicPlayerViewModel : ViewModel() {

    private val repository = MusicRepository.getInstance()

    private var _behaviorState = MutableLiveData(BottomSheetBehavior.STATE_COLLAPSED)
    val behaviorState: LiveData<Int> = _behaviorState

    private var _behaviorOffset = MutableLiveData(0f)
    val behaviorOffset: LiveData<Float> = _behaviorOffset

    fun updateState(state: Int) {
        _behaviorState.value = state
    }

    fun updateOffset(offset: Float) {
        _behaviorOffset.value = offset
    }

}