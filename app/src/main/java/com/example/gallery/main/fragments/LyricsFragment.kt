package com.example.gallery.main.fragments

import android.os.Bundle
import android.view.View
import com.example.gallery.BR
import com.example.gallery.R
import com.example.gallery.base.bindings.BindingConfig
import com.example.gallery.base.ui.BaseFragment
import com.example.gallery.main.state.LyricsFragmentViewModel
import com.example.gallery.main.state.MainActivityViewModel
import kotlinx.android.synthetic.main.fragment_lyrics.*

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
        if (state.music.value == null) return
        viewModel.getLyric(state.music.value!!)
        observe()
    }

    private fun observe() {
        viewModel.lyrics.observe(viewLifecycleOwner) {
            lyrics_view.data = it
        }
    }
}