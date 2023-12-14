package com.example.gallery.main.album.fragments

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.fragment.app.viewModels
import com.example.gallery.base.ui.pge.BaseFragment
import com.example.gallery.base.utils.AnimationUtils.onAnimationEnd
import com.example.gallery.databinding.FragmentPreviewBinding
import com.example.gallery.main.album.models.AlbumItemModel
import com.example.gallery.main.album.viewModels.PreviewFragmentViewModel
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.request.ImageRequestBuilder

class PreviewFragment(
    private val view: View,
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

    private fun initView() {
        binding.container.setOnClickListener { }
        val uri = Uri.parse("file://${imageItemModel.path}")
        val imageRequest = ImageRequestBuilder
            .newBuilderWithSource(uri)
            .setProgressiveRenderingEnabled(true)
            .build()
        binding.imageHide.controller = Fresco.newDraweeControllerBuilder()
            .setImageRequest(imageRequest)
            .build()
        val animation = AlphaAnimation(0f, 1f)
        animation.duration = 500
        binding.container.startAnimation(animation)
    }

    fun onBackPressed(afterDispatch: () -> Unit) {
        val animation = AlphaAnimation(1f, 0f)
        animation.duration = 200
        animation.onAnimationEnd { afterDispatch() }
        binding.container.startAnimation(animation)
    }
}