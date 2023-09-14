package com.example.gallery.main.music.fragments

import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.core.view.doOnLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.gallery.R
import com.example.gallery.base.ui.pge.BaseFragment
import com.example.gallery.base.utils.ViewUtils.getAlbumBitmap
import com.example.gallery.base.utils.ViewUtils.loadAlbumCover
import com.example.gallery.base.utils.ViewUtils.setHeight
import com.example.gallery.base.utils.ViewUtils.setMargins
import com.example.gallery.base.utils.ViewUtils.setWidth
import com.example.gallery.databinding.FragmentPlayerBinding
import com.example.gallery.main.music.enums.PlayerViewState
import com.example.gallery.main.music.viewModels.MusicPlayerViewModel
import com.example.gallery.main.music.viewModels.MusicViewModel
import com.example.gallery.main.video.player.state.PlayState
import com.example.gallery.media.local.bean.Music
import com.example.gallery.media.remote.lyrics.Lyric
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class MusicPlayerFragment(private val containerView: View) : BaseFragment() {
    private val state: MusicViewModel by lazy { getActivityScopeViewModel(MusicViewModel::class.java) }
    private val viewModel: MusicPlayerViewModel by viewModels()
    private val behavior: BottomSheetBehavior<View> by lazy { BottomSheetBehavior.from(containerView) }
    override val binding: FragmentPlayerBinding by lazy { FragmentPlayerBinding.inflate(layoutInflater) }
    private val bottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) = onSheetStateChanges(newState)
        override fun onSlide(bottomSheet: View, slideOffset: Float) = onSheetSlides(slideOffset)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        initView()
    }

    private fun initView() {
        binding.play.setOnClickListener { state.onPlayPressed() }
        binding.next.setOnClickListener { state.onNextPressed() }
        behavior.addBottomSheetCallback(bottomSheetBehaviorCallback)
        binding.album.setOnClickListener {
            if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED) behavior.state =
                BottomSheetBehavior.STATE_EXPANDED
            else viewModel.updateViewState()
        }
    }

    private fun observeViewModel() {
        state.music.observe(viewLifecycleOwner) {
            viewModel.initMusic(it)
            initPlayDetails(it)
        }
        state.state.observe(viewLifecycleOwner) {
            if (it == PlayState.PLAY) binding.play.setImageDrawable(
                ResourcesCompat.getDrawable(
                    requireActivity().resources,
                    R.drawable.ic_pause,
                    requireActivity().theme
                )
            ) else binding.play.setImageDrawable(
                ResourcesCompat.getDrawable(
                    requireActivity().resources,
                    R.drawable.ic_play,
                    requireActivity().theme
                )
            )
        }
        viewModel.lyrics.observe(viewLifecycleOwner) {
            binding.lyricsView.data = it
            lifecycleScope.launch { binding.lyricsView.start() }
        }
        viewModel.viewState.observe(viewLifecycleOwner) {
            when (it) {
                PlayerViewState.ALBUM -> {
                    ObjectAnimator.ofFloat(0f, 1f).apply {
                        duration = 200
                        addUpdateListener { animator -> changeAlbumCoverOffset(sqrt(animator.animatedValue as Float * animator.animatedValue as Float * animator.animatedValue as Float)) }
                        start()
                    }
                    binding.lyricsView.clearAnimation()
                    binding.lyricsView.animate()
                        .alphaBy(1f).alpha(0f)
                        .setDuration(200).start()
                    binding.songName.animate().alphaBy(0f).alpha(1f)
                        .translationYBy(4.dp.toFloat()).translationY(0f)
                        .setInterpolator { alpha -> alpha * alpha }.setDuration(200).start()
                    binding.singerName.animate().alphaBy(0f).alpha(1f)
                        .translationYBy(4.dp.toFloat()).translationY(0f)
                        .setInterpolator { alpha -> alpha * alpha }.setDuration(200).start()
                }

                PlayerViewState.LYRICS -> {
                    ObjectAnimator.ofFloat(1f, 0f).apply {
                        duration = 320
                        addUpdateListener { animator -> changeAlbumCoverOffset(animator.animatedValue as Float * animator.animatedValue as Float * animator.animatedValue as Float) }
                        start()
                    }
                    binding.lyricsView.clearAnimation()
                    binding.lyricsView.animate()
                        .alphaBy(0f).alpha(1f)
                        .setDuration(200).start()
                    binding.songName.animate().alphaBy(1f).alpha(0f)
                        .translationYBy(0f).translationY(8.dp.toFloat())
                        .setInterpolator { alpha -> sqrt(alpha) }.setDuration(320).start()
                    binding.singerName.animate().alphaBy(1f).alpha(0f)
                        .translationYBy(0f).translationY(8.dp.toFloat())
                        .setInterpolator { alpha -> sqrt(alpha) }.setDuration(320).start()
                }

                else -> {}
            }
        }
    }

    private fun onSheetStateChanges(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            binding.play.animate().alphaBy(0f).alpha(1f).setDuration(500)
                .setInterpolator(AccelerateDecelerateInterpolator()).start()
            binding.next.animate().alphaBy(0f).alpha(1f).setDuration(500)
                .setInterpolator(AccelerateDecelerateInterpolator()).start()
        } else {
            binding.play.animate().alphaBy(1f).alpha(0f).setDuration(500).start()
            binding.next.animate().alphaBy(1f).alpha(0f).setDuration(500).start()
        }
    }

    private fun onSheetSlides(slideOffset: Float) {
        changeSliderOffset(slideOffset)
        if (viewModel.viewState.value == PlayerViewState.LYRICS) return
        changeAlbumCoverOffset(slideOffset)
    }

    private fun changeSliderOffset(offset: Float) {
        val marginEnd = (96.dp * offset).toInt()
        binding.button.alpha = offset
        binding.button.setMargins(top = (24.dp * offset).toInt())
        binding.musicName.setMargins(start = 88.dp, end = marginEnd)
    }

    private fun changeAlbumCoverOffset(offset: Float) {
        val scale = 48 + (320 - 48) * offset * offset
        val marginTop = 96 * offset
        val marginStart = 24.dp + ((binding.root.width - 320.dp) / 2 - 24.dp) * offset
        binding.album.setWidth(scale.dp)
        binding.album.setHeight(scale.dp)
        binding.album.setMargins(marginStart.toInt(), marginTop.dp)
        binding.musicName.alpha = ((1 - offset) * (1 - offset))
        binding.album.radius = 4 + (8 - 4).dp.toFloat() * offset
    }

    private fun initPlayDetails(music: Music) {
        lifecycleScope.launch(Dispatchers.IO) { loadAlbumCover(music, binding.albumCover, 320.dp, 320.dp) }
        lifecycleScope.launch(Dispatchers.IO) {
            getAlbumBitmap(music).collect { launch(Dispatchers.Main) { binding.fluidView.initBackground(it) } }
        }
        binding.musicName.text = music.name
        binding.songName.text = music.name
        binding.singerName.text = music.singer
    }
}