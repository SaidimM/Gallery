package com.example.gallery.main.music.fragments

import LogUtil
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.View.MeasureSpec
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.gallery.R
import com.example.gallery.base.ui.pge.BaseFragment
import com.example.gallery.base.utils.AnimationUtils.setListeners
import com.example.gallery.base.utils.ViewUtils.loadAlbumCover
import com.example.gallery.base.utils.ViewUtils.setHeight
import com.example.gallery.base.utils.ViewUtils.setMargins
import com.example.gallery.base.utils.ViewUtils.setWidth
import com.example.gallery.databinding.FragmentPlayerBinding
import com.example.gallery.main.music.enums.ControllerState
import com.example.gallery.main.music.enums.PlayerViewState
import com.example.gallery.main.music.viewModels.MusicPlayerViewModel
import com.example.gallery.main.music.viewModels.MusicViewModel
import com.example.gallery.main.music.views.EaseCubicInterpolator
import com.example.gallery.media.music.local.bean.Music
import com.example.gallery.player.enums.PlayState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MusicPlayerFragment : BaseFragment() {
    private val state: MusicViewModel by lazy { getActivityScopeViewModel(MusicViewModel::class.java) }
    private val viewModel: MusicPlayerViewModel by viewModels()
    override val binding: FragmentPlayerBinding by lazy { FragmentPlayerBinding.inflate(layoutInflater) }
    private val bottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) = onSheetStateChanges(newState)
        override fun onSlide(bottomSheet: View, slideOffset: Float) = onSheetSlides(slideOffset)
    }
    private val bezierInterpolator = EaseCubicInterpolator(0.25f, 0.25f, 0.15f, 1f)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        initView()
    }

    private fun initView() {
        binding.viewModel = viewModel
        binding.state = state
        binding.play.setOnClickListener { state.onPlayPressed() }
        binding.lyricsView.setDragListener { animateController() }
    }

    private fun observeViewModel() {
        state.music.observe(viewLifecycleOwner) {
            initPlayDetails(it)
            if (state.controllerState.value == ControllerState.HIDDEN) state.updateController(state = ControllerState.SHOWING)
        }
        state.playState.observe(viewLifecycleOwner) {
            if (it == PlayState.PLAYING) binding.play.setImageDrawable(
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
        state.controllerOffset.observe(viewLifecycleOwner) { onSheetSlides(it) }
        viewModel.lyrics.observe(viewLifecycleOwner) {
            binding.lyricsView.data = it
            binding.lyricsView.measure(MeasureSpec.EXACTLY, MeasureSpec.EXACTLY)
            binding.lyricsView.layout(
                binding.lyricsView.left,
                binding.lyricsView.top,
                binding.lyricsView.right,
                binding.lyricsView.bottom
            )
            lifecycleScope.launch { binding.lyricsView.start() }
            animateController()
        }
        viewModel.viewState.observe(viewLifecycleOwner) {
            when (it) {
                PlayerViewState.ALBUM -> {
                    ObjectAnimator.ofFloat(0f, 1f).apply {
                        interpolator = bezierInterpolator
                        duration = 500
                        addUpdateListener { animator -> changeAlbumCoverOffset(animator.animatedValue as Float) }
                        start()
                    }
                    binding.lyricsView.clearAnimation()
                    binding.lyricsView.animate().alphaBy(1f).alpha(0f).setDuration(500).start()
                    binding.songName.animate().alphaBy(0f).alpha(1f)
                        .translationYBy(4.dp.toFloat()).translationY(0f)
                        .setInterpolator(bezierInterpolator).setDuration(500).start()
                    binding.singerName.animate().alphaBy(0f).alpha(1f)
                        .translationYBy(4.dp.toFloat()).translationY(0f)
                        .setInterpolator(bezierInterpolator).setDuration(500).start()
                }

                PlayerViewState.LYRICS -> {
                    ObjectAnimator.ofFloat(1f, 0f).apply {
                        interpolator = bezierInterpolator
                        duration = 380
                        addUpdateListener { animator -> changeAlbumCoverOffset(animator.animatedValue as Float) }
                        start()
                    }
                    binding.lyricsView.clearAnimation()
                    binding.lyricsView.animate().alphaBy(0f).alpha(1f).setDuration(200).start()
                    binding.songName.animate().alphaBy(1f).alpha(0f)
                        .translationYBy(0f).translationY(8.dp.toFloat())
                        .setInterpolator(bezierInterpolator).setDuration(320).start()
                    binding.singerName.animate().alphaBy(1f).alpha(0f)
                        .translationYBy(0f).translationY(8.dp.toFloat())
                        .setInterpolator(bezierInterpolator).setDuration(320).start()
                }

                else -> {}
            }
        }
    }

    private fun onSheetStateChanges(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_COLLAPSED)
            binding.play.animate().alphaBy(0f).alpha(1f).setDuration(200)
                .setInterpolator(AccelerateDecelerateInterpolator()).start()
        else binding.play.animate().alphaBy(1f).alpha(0f).setDuration(200).start()
    }

    private fun onSheetSlides(slideOffset: Float) {
        changeSliderOffset(slideOffset)
        if (viewModel.viewState.value == PlayerViewState.LYRICS) return
        changeAlbumCoverOffset(slideOffset)
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

    private fun animateController(): Boolean {
        lifecycleScope.launch(Dispatchers.Main) {
            if (binding.controller.visibility != View.VISIBLE) binding.controller.animate().alphaBy(0f).alpha(1f)
                .setDuration(500)
                .setListeners(onStart = { binding.controller.visibility = View.VISIBLE }).start()
            delay(5000)
            binding.controller.animate().alphaBy(1f).alpha(0f).setDuration(500)
                .setListeners(onStart = { binding.controller.visibility = View.GONE }).start()
        }
        return true
    }

    private fun initPlayDetails(music: Music) {
        lifecycleScope.launch(Dispatchers.IO) { loadAlbumCover(music, binding.albumCover, 320.dp, 320.dp) }
        binding.musicName.text = music.name
        binding.songName.text = music.name
        binding.singerName.text = music.singer
    }
}