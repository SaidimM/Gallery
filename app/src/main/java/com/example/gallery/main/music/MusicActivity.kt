package com.example.gallery.main.music

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentContainerView
import com.example.gallery.base.ui.pge.BaseActivity
import com.example.gallery.databinding.ActivityMusicBinding
import com.example.gallery.main.music.fragments.MusicPlayerFragment
import com.example.gallery.main.music.viewModels.MusicViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior


class MusicActivity : BaseActivity() {
    private val viewModel: MusicViewModel by lazy { getActivityScopeViewModel(MusicViewModel::class.java) }
    override val binding: ActivityMusicBinding by lazy { ActivityMusicBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initView()
    }

    private fun initData() {
        viewModel.loadMusic()
    }

    private fun initView() {
        val playerFragment = MusicPlayerFragment(binding.playerLayout)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(binding.playerLayout.id, playerFragment).commit()
    }

    override fun onStop() {
        viewModel.recyclePlayer()
        super.onStop()
    }
}