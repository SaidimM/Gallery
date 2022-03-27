package com.example.gallery.main.fragments

import androidx.lifecycle.ViewModelProvider
import com.example.gallery.BR
import com.example.gallery.R
import com.example.gallery.base.bindings.BindingConfig
import com.example.gallery.base.ui.BaseFragment
import com.example.gallery.main.state.MainActivityViewModel
import com.example.gallery.main.state.MusicVideoViewModel
import com.example.gallery.media.MediaViewModel

class MusicVideoFragment : BaseFragment() {
    private lateinit var viewModel: MusicVideoViewModel
    private lateinit var state: MainActivityViewModel
    private lateinit var media: MediaViewModel
    override fun initViewModel() {
        viewModel = getFragmentScopeViewModel(MusicVideoViewModel::class.java)
        state = getActivityScopeViewModel(MainActivityViewModel::class.java)
        media = ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application)
            .create(MediaViewModel::class.java)
    }

    override fun getBindingConfig() = BindingConfig(R.layout.fragment_music_video, BR.viewModel, viewModel)


}