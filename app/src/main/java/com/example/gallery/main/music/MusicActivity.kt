package com.example.gallery.main.music

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.marginBottom
import androidx.fragment.app.FragmentContainerView
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
    private val behavior: BottomSheetBehavior<FragmentContainerView> by lazy { BottomSheetBehavior.from(binding.cardView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initView()
    }

    private fun initData() {
        viewModel.loadMusic()
        viewModel.getLastPlayedMusic()
    }

    private fun initView() {
        title = getString(R.string.music)
        val playerFragment = MusicPlayerFragment(binding.cardView)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(binding.cardView.id, playerFragment).commit()
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
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

    override fun observe() {
        viewModel.music.observe(this) {
            binding.cardView.visibility = View.VISIBLE
            binding.fragmentList.setPadding(0, 0, 0, behavior.peekHeight)
        }
    }
}