package com.example.gallery.main.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.example.gallery.BR
import com.example.gallery.R
import com.example.gallery.Strings.LYRIC_DIR
import com.example.gallery.base.bindings.BindingConfig
import com.example.gallery.base.ui.BaseFragment
import com.example.gallery.main.state.LyricsFragmentViewModel
import com.example.gallery.main.state.MainActivityViewModel
import kotlinx.android.synthetic.main.fragment_lyrics.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class LyricsFragment: BaseFragment() {
    private lateinit var viewModel: LyricsFragmentViewModel
    private lateinit var state: MainActivityViewModel

    override fun getBindingConfig() = BindingConfig(R.layout.fragment_lyrics, BR.viewModel, viewModel)
    override fun initViewModel() {
        viewModel = getFragmentScopeViewModel(LyricsFragmentViewModel::class.java)
        state = getActivityScopeViewModel(MainActivityViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val music = state.music.value ?: return
        if (!File(LYRIC_DIR + music.mediaId + ".txt").exists()) state.saveLyric(music)
        viewModel.getLyric(music)
        observe()
    }

    private fun observe() {
        viewModel.lyrics.observe(viewLifecycleOwner) {
            lifecycleScope.launch(Dispatchers.Main) { lyrics_view.data = it }
        }
    }
}