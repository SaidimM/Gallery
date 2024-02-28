package com.example.gallery.main.music.views

import LogUtil
import android.animation.ObjectAnimator
import com.example.gallery.Constants
import com.example.gallery.base.utils.ViewUtils.dp
import com.example.gallery.base.utils.ViewUtils.setHeight
import com.example.gallery.base.utils.ViewUtils.setMargins
import com.example.gallery.base.utils.ViewUtils.setWidth
import com.example.gallery.databinding.FragmentPlayerBinding
import com.example.gallery.main.music.enums.ControllerState
import com.example.gallery.main.music.enums.PlayerViewState

class MusicFragmentControllerDispatcher(private val binding: FragmentPlayerBinding) {
    private val TAG = "MusicControllerDispatcher"

    private var playerViewState: PlayerViewState = PlayerViewState.ALBUM

    fun updateControllerOffset(offset: Float) {
        LogUtil.d(TAG, "updateControllerOffset, offset: $offset")
        changeSliderOffset(offset)
        changeAlbumCoverOffset(offset)
    }

    fun updateControllerState(state: ControllerState) {
        LogUtil.d(TAG, "updateControllerState, state: $state")
    }

    fun updateViewState(state: PlayerViewState) {
        when (state) {
            PlayerViewState.ALBUM -> {
                ObjectAnimator.ofFloat(0f, 1f).apply {
                    interpolator = Constants.bezierInterpolator
                    duration = 500
                    addUpdateListener { animator -> changeAlbumCoverOffset(animator.animatedValue as Float) }
                    start()
                }
                binding.lyricsView.clearAnimation()
                binding.lyricsView.animate().alphaBy(1f).alpha(0f).setDuration(500).start()
                binding.songName.animate().alphaBy(0f).alpha(1f)
                    .translationYBy(4.dp.toFloat()).translationY(0f)
                    .setInterpolator(Constants.bezierInterpolator).setDuration(500).start()
                binding.singerName.animate().alphaBy(0f).alpha(1f)
                    .translationYBy(4.dp.toFloat()).translationY(0f)
                    .setInterpolator(Constants.bezierInterpolator).setDuration(500).start()
            }

            PlayerViewState.LYRICS -> {
                ObjectAnimator.ofFloat(1f, 0f).apply {
                    interpolator = Constants.bezierInterpolator
                    duration = 380
                    addUpdateListener { animator -> changeAlbumCoverOffset(animator.animatedValue as Float) }
                    start()
                }
                binding.lyricsView.clearAnimation()
                binding.lyricsView.animate().alphaBy(0f).alpha(1f).setDuration(200).start()
                binding.songName.animate().alphaBy(1f).alpha(0f)
                    .translationYBy(0f).translationY(8.dp.toFloat())
                    .setInterpolator(Constants.bezierInterpolator).setDuration(320).start()
                binding.singerName.animate().alphaBy(1f).alpha(0f)
                    .translationYBy(0f).translationY(8.dp.toFloat())
                    .setInterpolator(Constants.bezierInterpolator).setDuration(320).start()
            }

            PlayerViewState.LIST -> {}
        }
    }

    private fun changeSliderOffset(offset: Float) {
        val marginEnd = (96.dp * offset).toInt()
        binding.button.alpha = offset
        binding.musicName.setMargins(start = 88.dp, end = marginEnd)
        binding.root.setPadding(0, (32.dp * offset).toInt(), 0, 0)
    }

    private fun changeAlbumCoverOffset(offset: Float) {
        LogUtil.i(TAG, "offset: $offset")
        val albumWidth = 56 + (320 - 56) * offset
        val marginTop = 56 * offset
        val marginStart = 16.dp + ((binding.root.width - 320.dp) / 2 - 16.dp) * offset
        binding.album.setWidth(albumWidth.dp)
        binding.album.setHeight(albumWidth.dp)
        binding.album.setMargins(marginStart.toInt(), marginTop.dp)
        binding.musicName.alpha = ((1 - offset) * (1 - offset))
        binding.album.radius = 8 + (16 - 6).dp.toFloat() * offset
    }
}