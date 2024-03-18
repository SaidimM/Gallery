package com.example.gallery.main.music.fragments

import LogUtil
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.View.MeasureSpec
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.gallery.base.ui.pge.BaseFragment
import com.example.gallery.databinding.FragmentPlayerBinding
import com.example.gallery.main.music.enums.PlayerViewState
import com.example.gallery.main.music.viewModels.MusicPlayerViewModel
import com.example.gallery.main.music.viewModels.MusicViewModel
import com.example.gallery.main.music.views.MusicControllerGestureDetector
import com.example.gallery.main.music.views.MusicFragmentControllerDispatcher
import com.example.gallery.media.music.local.bean.Music
import kotlinx.coroutines.launch

class MusicPlayerFragment : BaseFragment() {
    private val state: MusicViewModel by lazy { getActivityScopeViewModel(MusicViewModel::class.java) }
    private val viewModel: MusicPlayerViewModel by viewModels()
    override val binding: FragmentPlayerBinding by lazy { FragmentPlayerBinding.inflate(layoutInflater) }
    private val gestureDetector by lazy { MusicControllerGestureDetector(requireContext(), state) }
    private val dispatcher by lazy { MusicFragmentControllerDispatcher(binding, viewModel) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        binding.viewModel = viewModel
        binding.state = state
        binding.album.setOnTouchListener { v, event -> gestureDetector.onTouchEvent(event) }
        gestureDetector.onSingleTapListener = { gestureDetector.expandController() || dispatcher.onSingleTap() }
    }

    private fun observeViewModel() {
        state.music.observe(viewLifecycleOwner) { initPlayDetails(it) }
        state.playState.observe(viewLifecycleOwner) { LogUtil.d(TAG, "observeViewModel, playState: $it") }
        state.controllerState.observe(viewLifecycleOwner) { dispatcher.updateControllerState(it) }
        state.controllerOffset.observe(viewLifecycleOwner) { dispatcher.updateControllerOffset(it) }
        viewModel.lyrics.observe(viewLifecycleOwner) {
            binding.lyricsView.data = it
            binding.lyricsView.measure(MeasureSpec.EXACTLY, MeasureSpec.EXACTLY)
            binding.lyricsView.layout(
                binding.lyricsView.left, binding.lyricsView.top, binding.lyricsView.right, binding.lyricsView.bottom
            )
            lifecycleScope.launch { binding.lyricsView.start() }
        }
        viewModel.albumCover.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            Glide.with(requireContext()).load(it).into(binding.albumCover)
            binding.fluidView.initBackground(it)
        }
        viewModel.viewState.observe(this) {
            binding.lyricsView.visibility = if (it == PlayerViewState.LYRICS) View.VISIBLE else View.GONE
        }
    }

    private fun initPlayDetails(music: Music) {
        LogUtil.i(TAG, "music: ${music.name}")
        viewModel.getLyrics(music)
        viewModel.getAlbumCover(music)
        binding.musicName.text = music.name
        binding.songName.text = music.name
        binding.singerName.text = music.singer
    }
}