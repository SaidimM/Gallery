package com.example.gallery.main.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ScreenUtils
import com.bumptech.glide.Glide
import com.example.gallery.BR
import com.example.gallery.R
import com.example.gallery.base.bindings.BindingConfig
import com.example.gallery.base.ui.pge.BaseFragment
import com.example.gallery.base.utils.blurHash.BlurHashDecoder
import com.example.gallery.databinding.FragmentLyricsBinding
import com.example.gallery.databinding.FragmentMainBinding
import com.example.gallery.main.state.LyricsFragmentViewModel
import com.example.gallery.main.state.MusicViewModel
import com.example.gallery.media.local.bean.Music
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LyricsFragment : BaseFragment() {
    private val viewModel: LyricsFragmentViewModel by viewModels()
    private val state: MusicViewModel by viewModels()

    private lateinit var music: Music

    override val binding: FragmentLyricsBinding by lazy { FragmentLyricsBinding.inflate(layoutInflater) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMusic()
        initBackground()
        observeData()
    }

    private fun initBackground() {
        val density = ScreenUtils.getScreenDensity()
        val bitmap = BlurHashDecoder.decode(music.albumCoverBlurHash, 10, (density * 10).toInt())
        if (bitmap != null) Glide.with(requireContext()).load(bitmap).centerCrop().into(binding.back)
        binding.back.alpha = 0.5f
    }

    private fun initMusic() {
        music = state.music.value ?: return
        viewModel.setMusic(music)
        val isFileExists = viewModel.getLyric()
        if (!isFileExists) state.saveLyric(music)
        state.loadAlbumCover(music, binding.albumCover)
    }

    private fun observeData() {
        viewModel.lyrics.observe(viewLifecycleOwner) {
            lifecycleScope.launch(Dispatchers.Main) {
                binding.lyricsView.data = it
                binding.lyricsView.setOnDoubleTapListener { state.musicPlayer.seekTo(it) }
            }
        }
    }
}