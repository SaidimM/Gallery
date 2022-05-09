package com.example.gallery.main.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.Utils
import com.bumptech.glide.Glide
import com.example.gallery.BR
import com.example.gallery.R
import com.example.gallery.Strings
import com.example.gallery.Strings.LYRIC_DIR
import com.example.gallery.base.bindings.BindingConfig
import com.example.gallery.base.ui.BaseFragment
import com.example.gallery.base.utils.LocalMusicUtils
import com.example.gallery.main.state.LyricsFragmentViewModel
import com.example.gallery.main.state.MainActivityViewModel
import com.example.gallery.media.local.Music
import kotlinx.android.synthetic.main.fragment_lyrics.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File

class LyricsFragment: BaseFragment() {
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
        observe()
    }

    private fun initMusic() {
        if (viewModel.music.value == null) return
        if (!File(LYRIC_DIR + viewModel.music.value?.mediaId + ".txt").exists()) state.saveLyric(viewModel.music.value!!)
        viewModel.getLyric(viewModel.music.value!!)
        setMusicCover()
    }

    private fun setMusicCover() {
        val path = Strings.ALBUM_COVER_DIR + music.mediaAlbumId + ".jpg"
        doAsync {
            val bitmap = LocalMusicUtils.getArtwork(
                Utils.getApp(),
                music.id,
                music.albumId,
                allowdefalut = true,
                small = false
            )
            if (bitmap != null) uiThread {
                Glide.with(requireContext()).load(bitmap).into(album_cover)
            }
            else if (File(path).exists()) uiThread {
                Glide.with(requireContext()).load(path).into(album_cover)
            } else state.saveAlbumImage(music, album_cover)
        }
    }

    private fun observe() {
        viewModel.lyrics.observe(viewLifecycleOwner) {
            lifecycleScope.launch(Dispatchers.Main) { lyrics_view.data = it }
        }
    }
}