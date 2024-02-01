package com.example.gallery.main.music.fragments

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.View.MeasureSpec
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Palette.Swatch
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gallery.R
import com.example.gallery.base.ui.pge.BaseFragment
import com.example.gallery.base.ui.pge.BaseRecyclerViewAdapter
import com.example.gallery.base.utils.AnimationUtils.setListeners
import com.example.gallery.base.utils.ViewUtils.getAlbumBitmap
import com.example.gallery.base.utils.ViewUtils.loadAlbumCover
import com.example.gallery.base.utils.ViewUtils.setHeight
import com.example.gallery.base.utils.ViewUtils.setMargins
import com.example.gallery.base.utils.ViewUtils.setWidth
import com.example.gallery.databinding.FragmentPlayerBinding
import com.example.gallery.databinding.ItemColorBinding
import com.example.gallery.main.music.enums.PlayerViewState
import com.example.gallery.main.music.viewModels.MusicPlayerViewModel
import com.example.gallery.main.music.viewModels.MusicViewModel
import com.example.gallery.main.music.views.EaseCubicInterpolator
import com.example.gallery.main.video.player.state.PlayState
import com.example.gallery.media.music.local.bean.Music
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MusicPlayerFragment(private val containerView: View) : BaseFragment() {
    private val state: MusicViewModel by lazy { getActivityScopeViewModel(MusicViewModel::class.java) }
    private val viewModel: MusicPlayerViewModel by viewModels()
    private val behavior: BottomSheetBehavior<View> by lazy { BottomSheetBehavior.from(containerView) }
    override val binding: FragmentPlayerBinding by lazy { FragmentPlayerBinding.inflate(layoutInflater) }
    private val bottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) = onSheetStateChanges(newState)
        override fun onSlide(bottomSheet: View, slideOffset: Float) = onSheetSlides(slideOffset)
    }
    private val bezierInterpolator = EaseCubicInterpolator(0.25f,0.25f,0.15f,1f)
    private val colorAdapter by lazy {
        object : BaseRecyclerViewAdapter<Swatch?, ItemColorBinding>(requireContext()) {
            override fun getResourceId(viewType: Int) = R.layout.item_color
            override fun onBindItem(binding: ItemColorBinding, item: Swatch?, position: Int) {
                binding.setColor(item?.rgb ?: Color.WHITE)
            }
        }
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
        binding.album.setOnClickListener {
            if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED) behavior.state =
                BottomSheetBehavior.STATE_EXPANDED
            else viewModel.updateViewState()
        }
        binding.lyricsView.setDragListener { animateController() }
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = colorAdapter
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
            binding.lyricsView.data = it
            binding.lyricsView.measure(MeasureSpec.EXACTLY, MeasureSpec.EXACTLY)
            binding.lyricsView.layout(binding.lyricsView.left, binding.lyricsView.top, binding.lyricsView.right, binding.lyricsView.bottom)
            lifecycleScope.launch { binding.lyricsView.start() }
            animateController()
        }
        viewModel.viewState.observe(viewLifecycleOwner) {
            when (it) {
                PlayerViewState.ALBUM -> {
                    ObjectAnimator.ofFloat(0f, 1f).apply {
                        interpolator = bezierInterpolator
                        duration = 500
                        addUpdateListener { animator -> changeAlbumCoverOffset(animator.animatedValue as Float) }
                        start()
                    }
                    binding.lyricsView.clearAnimation()
                    binding.lyricsView.animate().alphaBy(1f).alpha(0f).setDuration(200).start()
                    binding.songName.animate().alphaBy(0f).alpha(1f)
                        .translationYBy(4.dp.toFloat()).translationY(0f)
                        .setInterpolator(bezierInterpolator).setDuration(200).start()
                    binding.singerName.animate().alphaBy(0f).alpha(1f)
                        .translationYBy(4.dp.toFloat()).translationY(0f)
                        .setInterpolator(bezierInterpolator).setDuration(200).start()
                    binding.recyclerView.animate().alphaBy(0f).alpha(1f).setDuration(200).start()
                }

                PlayerViewState.LYRICS -> {
                    ObjectAnimator.ofFloat(1f, 0f).apply {
                        interpolator = bezierInterpolator
                        duration = 500
                        addUpdateListener { animator -> changeAlbumCoverOffset(animator.animatedValue as Float) }
                        start()
                    }
                    binding.lyricsView.clearAnimation()
                    binding.lyricsView.animate().alphaBy(0f).alpha(1f).setDuration(200).start()
                    binding.songName.animate().alphaBy(1f).alpha(0f)
                        .translationYBy(0f).translationY(8.dp.toFloat())
                        .setInterpolator(bezierInterpolator).setDuration(320).start()
                    binding.singerName.animate().alphaBy(1f).alpha(0f)
                        .translationYBy(0f).translationY(8.dp.toFloat())
                        .setInterpolator(bezierInterpolator).setDuration(320).start()
                    binding.recyclerView.animate().alphaBy(1f).alpha(0f).setDuration(200).start()
                }

                else -> {}
            }
        }
    }

    private fun onSheetStateChanges(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            binding.play.animate().alphaBy(0f).alpha(1f).setDuration(200)
                .setInterpolator(AccelerateDecelerateInterpolator()).start()
            binding.next.animate().alphaBy(0f).alpha(1f).setDuration(200)
                .setInterpolator(AccelerateDecelerateInterpolator()).start()
        } else {
            binding.play.animate().alphaBy(1f).alpha(0f).setDuration(200).start()
            binding.next.animate().alphaBy(1f).alpha(0f).setDuration(200).start()
        }
    }

    private fun onSheetSlides(slideOffset: Float) {
        changeSliderOffset(slideOffset)
        if (viewModel.viewState.value == PlayerViewState.LYRICS) return
        changeAlbumCoverOffset(slideOffset)
    }

    private fun changeSliderOffset(offset: Float) {
        val marginEnd = (96.dp * offset).toInt()
        binding.button.alpha = offset
        binding.button.setMargins(top = (24.dp * offset).toInt())
        binding.musicName.setMargins(start = 88.dp, end = marginEnd)
    }

    private fun changeAlbumCoverOffset(offset: Float) {
        val scale = 56 + (320 - 56) * offset
        val marginTop = 96 * offset
        val marginStart = 32.dp + ((binding.root.width - 320.dp) / 2 - 32.dp) * offset
        binding.album.setWidth(scale.dp)
        binding.album.setHeight(scale.dp)
        binding.album.setMargins(marginStart.toInt(), marginTop.dp)
        binding.musicName.alpha = ((1 - offset) * (1 - offset))
        binding.album.radius = 8 + (16 - 6).dp.toFloat() * offset
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
        lifecycleScope.launch(Dispatchers.IO) {
            getAlbumBitmap(music).collect {
                launch(Dispatchers.Main) {
                    binding.fluidView.initBackground(it)
                    val palette = Palette.from(it).generate()
                    val colors = arrayListOf<Swatch?>()
                    colors.add(palette.vibrantSwatch)
                    colors.add(palette.mutedSwatch)
                    colors.add(palette.darkVibrantSwatch)
                    colors.add(palette.darkMutedSwatch)
                    colors.add(palette.lightVibrantSwatch)
                    colors.add(palette.lightMutedSwatch)
                    colorAdapter.data = colors
                }
            }
        }
        binding.musicName.text = music.name
        binding.songName.text = music.name
        binding.singerName.text = music.singer
    }
}