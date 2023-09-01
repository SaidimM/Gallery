package com.example.gallery.main.music.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.gallery.R
import com.example.gallery.base.ui.pge.BaseActivity
import com.example.gallery.base.ui.pge.BaseFragment
import com.example.gallery.base.utils.ViewUtils.loadAlbumCover
import com.example.gallery.databinding.FragmentPlayerBinding
import com.example.gallery.main.music.viewModels.MusicPlayerViewModel
import com.example.gallery.main.music.viewModels.MusicViewModel
import com.example.gallery.main.video.player.state.PlayState
import com.example.gallery.media.local.bean.Music
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicPlayerFragment(private val containerView: FragmentContainerView) : BaseFragment() {
    private val state: MusicViewModel by lazy { getActivityScopeViewModel(MusicViewModel::class.java) }
    private val viewModel: MusicPlayerViewModel by viewModels()
    private val behavior: BottomSheetBehavior<FragmentContainerView> by lazy { BottomSheetBehavior.from(containerView) }
    override val binding: FragmentPlayerBinding by lazy { FragmentPlayerBinding.inflate(layoutInflater) }

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
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.playerLayout.animate().alphaBy(0f).alpha(1f).setDuration(500).start()
                        (requireActivity() as BaseActivity).setStatusBarContent(true)
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.playerLayout.animate().alphaBy(1f).alpha(0f).setDuration(500).start()
                        (requireActivity() as BaseActivity).setStatusBarContent(false)
                    }
                    else -> {
                        binding.playerLayout.animate().alphaBy(1f).alpha(0f).setDuration(500).start()
                        (requireActivity() as BaseActivity).setStatusBarContent(true)
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    private fun observeViewModel() {
        state.music.observe(viewLifecycleOwner) {
            var bitmap: Bitmap?
            lifecycleScope.launchWhenCreated {
                bitmap = withContext(Dispatchers.Main) { loadAlbumCover(it, binding.albumCover) }
                binding.fluidView.initBackground(bitmap!!)
            }
            binding.musicName.text = it.name
            initPlayDetails(it)
        }
        state.state.observe(viewLifecycleOwner) {
            if (it == PlayState.PLAY) binding.play.setImageDrawable(
                ResourcesCompat.getDrawable(
                    requireActivity().resources,
                    R.drawable.ic_pause,
                    requireActivity().theme
                )
            )
            else binding.play.setImageDrawable(
                ResourcesCompat.getDrawable(
                    requireActivity().resources,
                    R.drawable.ic_play,
                    requireActivity().theme
                )
            )
        }
    }

    private fun initPlayDetails(music: Music) {
        lifecycleScope.launch {
            state.music.value?.let { loadAlbumCover(music, binding.largeCover) }
        }
        binding.songName.text = music.name
        binding.singerName.text = music.singer
    }
}