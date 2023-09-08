package com.example.gallery.main.music.fragments

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.gallery.R
import com.example.gallery.base.ui.pge.BaseFragment
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
        override fun onStateChanged(bottomSheet: View, newState: Int) {}
        override fun onSlide(bottomSheet: View, slideOffset: Float) = onSheetSlides(slideOffset)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        initView()
    }

    private fun initView() {
        binding.play.setOnClickListener { state.onPlayPressed() }
        binding.next.setOnClickListener { state.onNextPressed() }
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
            }
        }
    }

    private fun onSheetSlides(slideOffset: Float) {
        val alpha = ((1 - slideOffset) * (1 - slideOffset))
        binding.musicName.alpha = alpha
        binding.play.alpha = alpha
        binding.next.alpha = alpha
        binding.album.radius = 4 + (8 - 4).dp.toFloat() * slideOffset
        val layoutParams = binding.album.layoutParams as ConstraintLayout.LayoutParams
        val scale = 48 + (320 - 48) * slideOffset * slideOffset
        val marginTop = 16 + (96 - 16) * slideOffset
        val marginStart = 16.dp + ((binding.root.width - 320.dp) / 2 - 16.dp) * slideOffset
        layoutParams.width = scale.dp
        layoutParams.height = scale.dp
        layoutParams.setMargins(marginStart.toInt(), marginTop.dp, 0, 0)
        binding.album.layoutParams = layoutParams
    }

    private fun initPlayDetails(music: Music) {
        lifecycleScope.launch(Dispatchers.IO) { loadAlbumCover(music, binding.albumCover, 320.dp, 320.dp) }
        lifecycleScope.launch(Dispatchers.IO) {
            val bitmap = getAlbumBitmap(music).flowOn(Dispatchers.IO).single()
            launch(Dispatchers.Main) { binding.fluidView.initBackground(bitmap) }
        }
        binding.musicName.text = music.name
        binding.songName.text = music.name
        binding.singerName.text = music.singer
    }
}