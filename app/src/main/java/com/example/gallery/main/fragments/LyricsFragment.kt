package com.example.gallery.main.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ScreenUtils
import com.bumptech.glide.Glide
import com.example.gallery.BR
import com.example.gallery.R
import com.example.gallery.base.bindings.BindingConfig
import com.example.gallery.base.ui.pge.BaseFragment
import com.example.gallery.base.utils.blurHash.BlurHashDecoder
import com.example.gallery.main.state.LyricsFragmentViewModel
import com.example.gallery.main.state.MainActivityViewModel
import com.example.gallery.media.local.Music
import kotlinx.android.synthetic.main.fragment_lyrics.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LyricsFragment : BaseFragment() {
    private lateinit var viewModel: LyricsFragmentViewModel
    private lateinit var state: MainActivityViewModel

    private lateinit var music: Music

    override fun getBindingConfig() = BindingConfig(R.layout.fragment_lyrics, BR.viewModel, viewModel)
    override fun initViewModel() {
        viewModel = getFragmentScopeViewModel(LyricsFragmentViewModel::class.java)
        state = getActivityScopeViewModel(MainActivityViewModel::class.java)
        music = state.music.value ?: return
        viewModel.setMusic(music)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMusic()
        initBackground()
        observeData()
    }

    private fun initBackground() {
        val density = ScreenUtils.getScreenDensity()
        val bitmap = BlurHashDecoder.decode(music.albumCoverBlurHash, 10, (density * 10).toInt())
        if (bitmap != null) Glide.with(requireContext()).load(bitmap).centerCrop().into(back)
        back.alpha = 0.5f
    }

    private fun initMusic() {
        val isFileExists = viewModel.getLyric()
        if (!isFileExists) state.saveLyric(music)
        state.loadAlbumCover(music, album_cover)
    }

    private fun observeData() {
        viewModel.lyrics.observe(viewLifecycleOwner) {
            lifecycleScope.launch(Dispatchers.Main) {
                lyrics_view.data = it
                lyrics_view.setOnDoubleTapListener { state.musicPlayer.seekTo(it) }
            }
        }
    }
}