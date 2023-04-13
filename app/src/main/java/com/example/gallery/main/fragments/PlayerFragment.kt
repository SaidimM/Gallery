package com.example.gallery.main.fragments

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom
import com.example.gallery.BR
import com.example.gallery.R
import com.example.gallery.base.bindings.BindingConfig
import com.example.gallery.base.ui.pge.BaseFragment
import com.example.gallery.main.state.MainActivityViewModel
import com.example.gallery.main.state.PlayerViewModel
import com.example.gallery.player.state.PlayState
import kotlinx.android.synthetic.main.fragment_player.*

class PlayerFragment : BaseFragment() {
    private lateinit var viewModel: PlayerViewModel
    private lateinit var state: MainActivityViewModel

    override fun initViewModel() {
        viewModel = getFragmentScopeViewModel(PlayerViewModel::class.java)
        state = getActivityScopeViewModel(MainActivityViewModel::class.java)
    }

    override fun getBindingConfig() = BindingConfig(R.layout.fragment_player, BR.viewModel, viewModel)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        initView()
    }

    private fun initView() {
        play.setOnClickListener { state.onPlayPressed() }
        next.setOnClickListener { state.onNextPressed() }
    }

    private fun observeViewModel() {
        state.music.observe(viewLifecycleOwner) {
            state.loadAlbumCover(it, album_cover)
            music_name.text = it.name
        }
        state.state.observe(viewLifecycleOwner) {
            if (it == PlayState.PLAY) play.background = requireActivity().getDrawable(R.drawable.ic_pause)
            else play.background = requireActivity().getDrawable(R.drawable.ic_play)
        }
    }
}