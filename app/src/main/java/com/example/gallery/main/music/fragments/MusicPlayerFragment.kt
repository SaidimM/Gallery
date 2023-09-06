package com.example.gallery.main.music.fragments

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.gallery.R
import com.example.gallery.base.ui.pge.BaseFragment
import com.example.gallery.base.utils.AnimationUtils.setListeners
import com.example.gallery.base.utils.ViewUtils.getAlbumBitmap
import com.example.gallery.base.utils.ViewUtils.loadAlbumCover
import com.example.gallery.databinding.FragmentPlayerBinding
import com.example.gallery.main.music.viewModels.MusicPlayerViewModel
import com.example.gallery.main.music.viewModels.MusicViewModel
import com.example.gallery.main.video.player.state.PlayState
import com.example.gallery.media.local.bean.Music
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch

class MusicPlayerFragment(private val containerView: View) : BaseFragment() {
    private val state: MusicViewModel by lazy { getActivityScopeViewModel(MusicViewModel::class.java) }
    private val viewModel: MusicPlayerViewModel by viewModels()
    private val behavior: BottomSheetBehavior<View> by lazy { BottomSheetBehavior.from(containerView) }
    override val binding: FragmentPlayerBinding by lazy { FragmentPlayerBinding.inflate(layoutInflater) }
    private val bottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    binding.playerLayout.animate().alphaBy(0f).alpha(1f).setDuration(500).start()
                    binding.largeCover.visibility = View.INVISIBLE
                }

                BottomSheetBehavior.STATE_EXPANDED -> {
                    binding.playerLayout.animate().alphaBy(1f).alpha(0f).setDuration(200).start()
                    binding.largeCover.startAnimation(
                        AnimationUtils.loadAnimation(requireContext(), R.anim.anim_large_cover)
                            .apply { this.setListeners(onStart = { binding.largeCover.visibility = View.VISIBLE }) })
                }

                else -> {
                    binding.playerLayout.animate().alphaBy(1f).alpha(0f).setDuration(300).start()
                }
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        initView()
    }

    private fun initView() {
        binding.play.setOnClickListener { state.onPlayPressed() }
        binding.next.setOnClickListener { state.onNextPressed() }
        binding.playerLayout.setOnClickListener {
            if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) behavior.state =
                BottomSheetBehavior.STATE_EXPANDED
        }
        behavior.addBottomSheetCallback(bottomSheetBehaviorCallback)
    }

    private fun observeViewModel() {
        state.music.observe(viewLifecycleOwner) {
            viewModel.initMusic(it)
            initPlayDetails(it)
        }
        state.state.observe(viewLifecycleOwner) {
            if (it == PlayState.PLAY) binding.play.setImageDrawable(
                ResourcesCompat.getDrawable(
                    requireActivity().resources,
                    R.drawable.ic_pause,
                    requireActivity().theme
                )
            ) else binding.play.setImageDrawable(
                ResourcesCompat.getDrawable(
                    requireActivity().resources,
                    R.drawable.ic_play,
                    requireActivity().theme
                )
            )
        }
        viewModel.lyrics.observe(viewLifecycleOwner) {
            lifecycleScope.launch(Dispatchers.Main) {
                binding.lyricsView.data = it
                binding.lyricsView.setOnDoubleTapListener { state.seekTo(it) }
            }
        }
    }

    private fun initPlayDetails(music: Music) {
        lifecycleScope.launch(Dispatchers.IO) { loadAlbumCover(music, binding.albumCover) }
        lifecycleScope.launch(Dispatchers.IO) { loadAlbumCover(music, binding.largeCover) }
        lifecycleScope.launch(Dispatchers.IO) {
            val bitmap = getAlbumBitmap(music).flowOn(Dispatchers.IO).single()
            launch(Dispatchers.Main) { binding.fluidView.initBackground(bitmap) }
        }
        binding.musicName.text = music.name
        binding.songName.text = music.name
        binding.singerName.text = music.singer
    }
}