package com.example.gallery.main.music.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.gallery.R
import com.example.gallery.base.ui.pge.BaseFragment
import com.example.gallery.databinding.FragmentPlayerBinding
import com.example.gallery.main.music.viewModels.MusicViewModel
import com.example.gallery.main.video.player.state.PlayState

class PlayerFragment : BaseFragment() {
    private val state: MusicViewModel by viewModels()
    override val binding: FragmentPlayerBinding by lazy { FragmentPlayerBinding.inflate(layoutInflater) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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