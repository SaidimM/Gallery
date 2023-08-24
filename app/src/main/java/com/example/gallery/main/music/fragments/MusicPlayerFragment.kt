package com.example.gallery.main.music.fragments

import LogUtil
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import com.blankj.utilcode.util.ConvertUtils.dp2px
import com.example.gallery.R
import com.example.gallery.base.ui.pge.BaseFragment
import com.example.gallery.base.utils.ViewUtils.loadAlbumCover
import com.example.gallery.databinding.FragmentPlayerBinding
import com.example.gallery.main.music.viewModels.MusicPlayerViewModel
import com.example.gallery.main.music.viewModels.MusicViewModel
import com.example.gallery.main.video.player.state.PlayState
import com.google.android.material.appbar.AppBarLayout.Behavior
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MusicPlayerFragment(private val containerView: FragmentContainerView) : BaseFragment() {
    private val state: MusicViewModel by lazy { getActivityScopeViewModel(MusicViewModel::class.java) }
    private val viewModel: MusicPlayerViewModel by viewModels()
    override val binding: FragmentPlayerBinding by lazy { FragmentPlayerBinding.inflate(layoutInflater) }
    private val behavior: BottomSheetBehavior<FragmentContainerView> by lazy { BottomSheetBehavior.from(containerView) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        initView()
    }

    private fun initView() {
        binding.play.setOnClickListener { state.onPlayPressed() }
        binding.next.setOnClickListener { state.onNextPressed() }
        binding.playerLayout.setOnClickListener {
            if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        behavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {}

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                LogUtil.d(javaClass.simpleName, "slideOffset: $slideOffset")
                val marginTop = slideOffset * dp2px(36f) - dp2px(4f)
                (binding.drag.layoutParams as ConstraintLayout.LayoutParams).apply { setMargins(0, marginTop.toInt(), 0, 0) }
            }
        })
    }

    private fun observeViewModel() {
        state.music.observe(viewLifecycleOwner) {
            var bitmap: Bitmap? = null
            lifecycleScope.launchWhenCreated {
                bitmap = withContext(Dispatchers.Main) { loadAlbumCover(it, binding.albumCover) }
                initPlayerBackground(binding.root, bitmap!!)
            }
            binding.musicName.text = it.name
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

    private fun initPlayerBackground(view: View, bitmap: Bitmap) {
        val palette = Palette.from(bitmap).generate()
        val color = view.backgroundTintList?.defaultColor
        view.background.setTint(palette.getVibrantColor(color ?: Color.WHITE))
    }
}