package com.example.gallery.main.music

import LogUtil
import android.os.Bundle
import com.example.gallery.R
import com.example.gallery.base.ui.pge.BaseActivity
import com.example.gallery.databinding.ActivityMusicBinding
import com.example.gallery.main.music.fragments.MusicPlayerFragment
import com.example.gallery.main.music.viewModels.MusicViewModel


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
        title = getString(R.string.music)
        val playerFragment = MusicPlayerFragment(binding.cardView)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(binding.playerLayout.id, playerFragment).commit()
    }

    override fun onStop() {
        viewModel.recyclePlayer()
        super.onStop()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        LogUtil.d("onBackPressed")
    }
}