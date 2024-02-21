package com.example.gallery.main.music.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.GestureDetector
import android.view.View
import android.view.View.MeasureSpec
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.gallery.R
import com.example.gallery.base.ui.pge.BaseFragment
import com.example.gallery.base.utils.AnimationUtils.setListeners
import com.example.gallery.base.utils.ViewUtils.loadAlbumCover
import com.example.gallery.databinding.FragmentPlayerBinding
import com.example.gallery.main.music.enums.ControllerState
import com.example.gallery.main.music.viewModels.MusicPlayerViewModel
import com.example.gallery.main.music.viewModels.MusicViewModel
import com.example.gallery.main.music.views.MusicControllerGestureDetector
import com.example.gallery.media.music.local.bean.Music
import com.example.gallery.player.enums.PlayState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MusicPlayerFragment : BaseFragment() {
    private val state: MusicViewModel by lazy { getActivityScopeViewModel(MusicViewModel::class.java) }
    private val viewModel: MusicPlayerViewModel by viewModels()
    override val binding: FragmentPlayerBinding by lazy { FragmentPlayerBinding.inflate(layoutInflater) }
    private val simpleGestureDetector by lazy { MusicControllerGestureDetector(binding) }
    private val gestureDetector by lazy { GestureDetector(requireContext(), simpleGestureDetector) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        binding.viewModel = viewModel
        binding.state = state
        binding.play.setOnClickListener { state.onPlayPressed() }
        binding.lyricsView.setDragListener { animateController() }
        binding.root.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
        simpleGestureDetector.stateChangedListener = { }
        simpleGestureDetector.offsetChangedListener = { }
    }

    private fun observeViewModel() {
        state.music.observe(viewLifecycleOwner) {
            initPlayDetails(it)
            simpleGestureDetector.updateState(ControllerState.SHOWING)
        }
        state.playState.observe(viewLifecycleOwner) {
            if (it == PlayState.PLAYING) binding.play.setImageDrawable(
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
            binding.lyricsView.data = it
            binding.lyricsView.measure(MeasureSpec.EXACTLY, MeasureSpec.EXACTLY)
            binding.lyricsView.layout(
                binding.lyricsView.left,
                binding.lyricsView.top,
                binding.lyricsView.right,
                binding.lyricsView.bottom
            )
            lifecycleScope.launch { binding.lyricsView.start() }
            animateController()
        }
    }

    private fun onSheetStateChanges(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_COLLAPSED)
            binding.play.animate().alphaBy(0f).alpha(1f).setDuration(200)
                .setInterpolator(AccelerateDecelerateInterpolator()).start()
        else binding.play.animate().alphaBy(1f).alpha(0f).setDuration(200).start()
    }

    private fun animateController(): Boolean {
        lifecycleScope.launch(Dispatchers.Main) {
            if (binding.controller.visibility != View.VISIBLE) binding.controller.animate().alphaBy(0f).alpha(1f)
                .setDuration(500)
                .setListeners(onStart = { binding.controller.visibility = View.VISIBLE }).start()
            delay(5000)
            binding.controller.animate().alphaBy(1f).alpha(0f).setDuration(500)
                .setListeners(onStart = { binding.controller.visibility = View.GONE }).start()
        }
        return true
    }

    private fun initPlayDetails(music: Music) {
        lifecycleScope.launch(Dispatchers.IO) { loadAlbumCover(music, binding.albumCover, 320.dp, 320.dp) }
        binding.musicName.text = music.name
        binding.songName.text = music.name
        binding.singerName.text = music.singer
    }
}