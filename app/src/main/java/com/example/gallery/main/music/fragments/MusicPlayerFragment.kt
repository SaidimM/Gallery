package com.example.gallery.main.music.fragments

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.gallery.R
import com.example.gallery.base.ui.pge.BaseFragment
import com.example.gallery.base.ui.pge.BaseRecyclerViewAdapter
import com.example.gallery.base.utils.ViewUtils.getAlbumBitmap
import com.example.gallery.base.utils.ViewUtils.loadAlbumCover
import com.example.gallery.databinding.FragmentPlayerBinding
import com.example.gallery.databinding.ItemLyricBinding
import com.example.gallery.main.music.enums.PlayerViewState
import com.example.gallery.main.music.viewModels.MusicPlayerViewModel
import com.example.gallery.main.music.viewModels.MusicViewModel
import com.example.gallery.main.video.player.state.PlayState
import com.example.gallery.media.local.bean.Music
import com.example.gallery.media.remote.lyrics.Lyric
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class MusicPlayerFragment(private val containerView: View) : BaseFragment() {
    private val state: MusicViewModel by lazy { getActivityScopeViewModel(MusicViewModel::class.java) }
    private val viewModel: MusicPlayerViewModel by viewModels()
    private val behavior: BottomSheetBehavior<View> by lazy { BottomSheetBehavior.from(containerView) }
    override val binding: FragmentPlayerBinding by lazy { FragmentPlayerBinding.inflate(layoutInflater) }
    private val bottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {}
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
        binding.album.setOnClickListener { viewModel.updateViewState() }
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
            lifecycleScope.launch(Dispatchers.Main) {
            }
        }
        viewModel.viewState.observe(viewLifecycleOwner) {
            when (it) {
                PlayerViewState.ALBUM -> {
                    ObjectAnimator.ofFloat(0f, 1f).apply {
                        duration = 200
                        addUpdateListener { animator -> changeAlbumCoverOffset(sqrt(animator.animatedValue as Float * animator.animatedValue as Float * animator.animatedValue as Float)) }
                        start()
                    }
                }
                PlayerViewState.LYRICS -> {
                    ObjectAnimator.ofFloat(1f, 0f).apply {
                        duration = 320
                        addUpdateListener { animator ->  changeAlbumCoverOffset(animator.animatedValue as Float * animator.animatedValue as Float * animator.animatedValue as Float) }
                        start()
                    }
                }
                else -> {}
            }
        }
    }

    private fun onSheetSlides(slideOffset: Float) {
        changeSliderOffset(slideOffset)
        if (viewModel.viewState.value == PlayerViewState.LYRICS) return
        changeAlbumCoverOffset(slideOffset)
    }

    private fun changeSliderOffset(offset: Float) {
        val viewHeight = 4.dp * offset
        val topPadding = 32.dp * offset
        val layoutParams = binding.slider.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.apply {
            height = 12.dp + viewHeight.toInt()
            setMargins(0, topPadding.toInt(), 0, 0)
        }
        binding.slider.layoutParams = layoutParams
    }

    private fun changeAlbumCoverOffset(offset: Float) {
        val alpha = ((1 - offset) * (1 - offset))
        binding.musicName.alpha = alpha
        binding.play.alpha = alpha
        binding.next.alpha = alpha
        binding.album.radius = 4 + (8 - 4).dp.toFloat() * offset
        val layoutParams = binding.album.layoutParams as ConstraintLayout.LayoutParams
        val scale = 48 + (320 - 48) * offset * offset
        val marginTop = 16 + (96 - 16) * offset
        val marginStart = 16.dp + ((binding.root.width - 320.dp) / 2 - 16.dp) * offset
        layoutParams.width = scale.dp
        layoutParams.height = scale.dp
        layoutParams.setMargins(marginStart.toInt(), marginTop.dp, 0, 0)
        binding.album.layoutParams = layoutParams
    }

    private fun initPlayDetails(music: Music) {
        lifecycleScope.launch(Dispatchers.IO) { loadAlbumCover(music, binding.albumCover, 320.dp, 320.dp) }
        lifecycleScope.launch(Dispatchers.IO) {
            val bitmap = getAlbumBitmap(music).flowOn(Dispatchers.IO).single()
            launch(Dispatchers.Main) { binding.fluidView.initBackground(bitmap) }
        }
        binding.musicName.text = music.name
        binding.songName.text = music.name
        binding.singerName.text = music.singer
    }
}