package com.example.gallery.main.album.fragments

import LogUtil
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.fragment.app.viewModels
import com.example.gallery.base.ui.pge.BaseFragment
import com.example.gallery.base.utils.AnimationUtils.onAnimationEnd
import com.example.gallery.databinding.FragmentPreviewBinding
import com.example.gallery.main.album.models.AlbumItemModel
import com.example.gallery.main.album.viewModels.PreviewFragmentViewModel


class PreviewFragment(
    private val imageItemModel: AlbumItemModel
) : BaseFragment() {
    private val viewModel: PreviewFragmentViewModel by viewModels()

    override val binding: FragmentPreviewBinding by lazy {
        FragmentPreviewBinding.inflate(
            layoutInflater
        )
    }

    var onClick: () -> Unit = {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        binding.preview.setImage(imageItemModel.uri)
    }

    fun onBackPressed(afterDispatch: () -> Unit) {
        val animation = AlphaAnimation(1f, 0f)
        animation.duration = 200
        animation.onAnimationEnd { afterDispatch() }
        binding.container.startAnimation(animation)
    }
}