package com.example.gallery.main.music.fragments

import LogUtil
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.View.MeasureSpec
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.gallery.base.ui.pge.BaseFragment
import com.example.gallery.base.utils.ViewUtils.loadAlbumCover
import com.example.gallery.databinding.FragmentPlayerBinding
import com.example.gallery.main.music.viewModels.MusicPlayerViewModel
import com.example.gallery.main.music.viewModels.MusicViewModel
import com.example.gallery.main.music.views.MusicFragmentControllerDispatcher
import com.example.gallery.media.music.local.bean.Music
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MusicPlayerFragment : BaseFragment() {
    private val state: MusicViewModel by lazy { getActivityScopeViewModel(MusicViewModel::class.java) }
    private val viewModel: MusicPlayerViewModel by viewModels()
    override val binding: FragmentPlayerBinding by lazy { FragmentPlayerBinding.inflate(layoutInflater) }
    private val dispatcher by lazy { MusicFragmentControllerDispatcher(binding) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        binding.viewModel = viewModel
        binding.state = state
    }

    private fun observeViewModel() {
        state.music.observe(viewLifecycleOwner) {
            initPlayDetails(it)
        }
        state.playState.observe(viewLifecycleOwner) {
            LogUtil.d(TAG, "observeViewModel, playState: $it")
        }
        state.controllerState.observe(viewLifecycleOwner) { dispatcher.updateControllerState(it) }
        state.controllerOffset.observe(viewLifecycleOwner) { dispatcher.updateControllerOffset(it) }
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
        }
    }

    private fun initPlayDetails(music: Music) {
        lifecycleScope.launch(Dispatchers.IO) { loadAlbumCover(music, binding.albumCover, 320.dp, 320.dp) }
        binding.musicName.text = music.name
        binding.songName.text = music.name
        binding.singerName.text = music.singer
    }
}