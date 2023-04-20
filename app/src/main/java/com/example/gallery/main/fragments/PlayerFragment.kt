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
import com.example.gallery.databinding.FragmentPlayerBinding
import com.example.gallery.main.state.MainActivityViewModel
import com.example.gallery.main.state.MusicViewModel
import com.example.gallery.main.state.PlayerViewModel
import com.example.gallery.main.views.player.state.PlayState

class PlayerFragment : BaseFragment() {
    private lateinit var viewModel: PlayerViewModel
    private lateinit var state: MusicViewModel
    private lateinit var binding: FragmentPlayerBinding

    override fun initViewModel() {
        viewModel = getFragmentScopeViewModel(PlayerViewModel::class.java)
        state = getActivityScopeViewModel(MusicViewModel::class.java)
    }

    override fun getBindingConfig() = BindingConfig(R.layout.fragment_player, BR.viewModel, viewModel)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getBinding() as FragmentPlayerBinding
        observeViewModel()
        initView()
    }

    private fun initView() {
        binding.play.setOnClickListener { state.onPlayPressed() }
        binding.next.setOnClickListener { state.onNextPressed() }
    }

    private fun observeViewModel() {
        state.music.observe(viewLifecycleOwner) {
            state.loadAlbumCover(it, binding.albumCover)
            binding.musicName.text = it.name
        }
        state.state.observe(viewLifecycleOwner) {
            if (it == PlayState.PLAY) binding.play.background = requireActivity().getDrawable(R.drawable.ic_pause)
            else binding.play.background = requireActivity().getDrawable(R.drawable.ic_play)
        }
    }
}