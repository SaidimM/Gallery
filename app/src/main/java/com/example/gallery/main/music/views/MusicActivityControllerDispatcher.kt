package com.example.gallery.main.music.views

import LogUtil
import android.animation.ObjectAnimator
import android.view.View
import androidx.core.animation.doOnEnd
import com.example.gallery.Constants
import com.example.gallery.base.utils.AnimationUtils.setListeners
import com.example.gallery.base.utils.ViewUtils.dp
import com.example.gallery.base.utils.ViewUtils.setHeight
import com.example.gallery.base.utils.ViewUtils.setMargins
import com.example.gallery.databinding.ActivityMusicBinding
import com.example.gallery.main.music.enums.ControllerState
import com.example.gallery.main.music.viewModels.MusicViewModel

class MusicActivityControllerDispatcher(
    private val binding: ActivityMusicBinding,
    private val viewModel: MusicViewModel
) {
    private val TAG = "MusicActivityControllerDispatcher"
    private val startMargin = 8.dp

    fun changeControllerOffset(offset: Float) {
        if (offset < 0) return
        val margin = (startMargin - startMargin * offset).toInt()
        val expendedHeight = binding.root.height
        val collapsedHeight = 88.dp
        val height = collapsedHeight + (expendedHeight - collapsedHeight) * offset
        binding.cardView.setMargins(margin, margin, margin, margin)
        binding.cardView.setHeight(height.toInt())
    }

    fun changeControllerState(state: ControllerState) {
        when (state) {
            ControllerState.SHOWING -> {
                LogUtil.d(TAG, "this state, state: $state")
                binding.cardView.animate().alphaBy(0f).alpha(1f).setDuration(1000)
                    .setListeners(onStart = { binding.cardView.visibility = View.VISIBLE },
                        onEnd = { viewModel.updateControllerState(ControllerState.COLLAPSED) }).start()
            }

            ControllerState.HIDDEN -> binding.cardView.alpha = 0f

            else -> {}
        }
    }
}