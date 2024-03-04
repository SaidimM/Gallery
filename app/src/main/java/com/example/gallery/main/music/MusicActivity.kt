package com.example.gallery.main.music

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.GestureDetector
import com.example.gallery.R
import com.example.gallery.base.ui.pge.BaseActivity
import com.example.gallery.databinding.ActivityMusicBinding
import com.example.gallery.main.music.enums.ControllerState
import com.example.gallery.main.music.viewModels.MusicViewModel
import com.example.gallery.main.music.views.MusicActivityControllerDispatcher
import com.example.gallery.main.music.views.MusicControllerGestureDetector

class MusicActivity : BaseActivity() {
    private val viewModel: MusicViewModel by lazy { getActivityScopeViewModel(MusicViewModel::class.java) }
    override val binding: ActivityMusicBinding by lazy { ActivityMusicBinding.inflate(layoutInflater) }
    private val gestureDetector by lazy { MusicControllerGestureDetector(this, viewModel) }
    private val dispatcher by lazy { MusicActivityControllerDispatcher(binding, viewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initView()
    }

    private fun initData() {
        viewModel.loadMusic()
        viewModel.getLastPlayedMusic()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        title = getString(R.string.music)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.cardView.setOnTouchListener { v, event -> gestureDetector.onTouchEvent(event) }
        gestureDetector.onSingleTapListener = { gestureDetector.expandController() }
    }

    override fun onStop() {
        viewModel.recyclePlayer()
        super.onStop()
    }

    override fun observe() {
        viewModel.music.observe(this) { viewModel.updateControllerState(ControllerState.SHOWING) }
        viewModel.controllerState.observe(this) { dispatcher.changeControllerState(it) }
        viewModel.controllerOffset.observe(this) { dispatcher.changeControllerOffset(it) }
    }

    override fun onBackPressed() {
        val dispatched = gestureDetector.collapseController()
        if (!dispatched) super.onBackPressed()
    }

}