package com.example.gallery.main.music

import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import com.example.gallery.R
import com.example.gallery.base.ui.pge.BaseActivity
import com.example.gallery.databinding.ActivityMusicBinding
import com.example.gallery.main.music.fragments.MusicPlayerFragment
import com.example.gallery.main.music.viewModels.MusicViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView


class MusicActivity : BaseActivity() {
    private val viewModel: MusicViewModel by lazy { getActivityScopeViewModel(MusicViewModel::class.java) }
    override val binding: ActivityMusicBinding by lazy { ActivityMusicBinding.inflate(layoutInflater) }
    private val behavior: BottomSheetBehavior<MaterialCardView> by lazy { BottomSheetBehavior.from(binding.cardView) }

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
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.toolbar.navigationIcon = AppCompatResources.getDrawable(this, R.drawable.ic_back)
    }

    override fun onStop() {
        viewModel.recyclePlayer()
        super.onStop()
    }

    override fun onBackPressed() {
        if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) behavior.state =
            BottomSheetBehavior.STATE_COLLAPSED
        else super.onBackPressed()
    }
}