package com.example.gallery.main.music

import android.annotation.SuppressLint
import android.os.Bundle
import com.example.gallery.R
import com.example.gallery.base.ui.pge.BaseActivity
import com.example.gallery.databinding.ActivityMusicBinding
import com.example.gallery.main.music.viewModels.MusicViewModel


class MusicActivity : BaseActivity() {
    private val viewModel: MusicViewModel by lazy { getActivityScopeViewModel(MusicViewModel::class.java) }
    override val binding: ActivityMusicBinding by lazy { ActivityMusicBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initView()
        windowManager
    }

    private fun initData() {
        viewModel.loadMusic()
        viewModel.getLastPlayedMusic()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        title = getString(R.string.music)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onStop() {
        viewModel.recyclePlayer()
        super.onStop()
    }

}