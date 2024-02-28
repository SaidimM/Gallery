package com.example.gallery.main.music.views

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
    private val viewModel: MusicViewModel) {
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
            ControllerState.EXPENDING -> {
                ObjectAnimator.ofFloat(0f, 1f).apply {
                    duration = 500
                    interpolator = Constants.bezierInterpolator
                    addUpdateListener { viewModel.updateControllerOffset(this.animatedValue as Float) }
                    doOnEnd { viewModel.updateControllerState(ControllerState.EXPENDED) }
                    start()
                }
            }

            ControllerState.COLLAPSING -> {
                ObjectAnimator.ofFloat(1f, 0f).apply {
                    duration = 320
                    interpolator = Constants.bezierInterpolator
                    addUpdateListener { viewModel.updateControllerOffset(this.animatedValue as Float) }
                    doOnEnd { viewModel.updateControllerState(ControllerState.COLLAPSED) }
                    start()
                }
            }

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

    fun backPressed(): Boolean {
        if (viewModel.controllerState.value == ControllerState.EXPENDED) {
            viewModel.updateControllerState(ControllerState.COLLAPSING)
            return true
        } else {
            return false
        }
    }
}