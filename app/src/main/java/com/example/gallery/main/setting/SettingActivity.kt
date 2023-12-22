package com.example.gallery.main.setting

import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.example.gallery.BR
import com.example.gallery.base.ui.pge.BaseActivity
import com.example.gallery.databinding.ActivitySettingBinding
import com.facebook.cache.disk.DiskCacheConfig
import com.facebook.common.util.ByteConstants
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.cache.MemoryCacheParams
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.request.ImageRequestBuilder

class SettingActivity : BaseActivity() {
    private val viewModel: SettingViewModel by viewModels()

    override val binding: ActivitySettingBinding by lazy { ActivitySettingBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        val uri = Uri.parse("file:///storage/emulated/0/20221002_161652546_iOS.jpg")
        val imageRequest = ImageRequestBuilder
            .newBuilderWithSource(uri)
            .build()
        binding.simpleDraweeView.controller = Fresco.newDraweeControllerBuilder()
            .setOldController(binding.simpleDraweeView.controller)
            .setImageRequest(imageRequest)
            .build()
        Glide.with(this).load(uri).into(binding.imageView)
    }

}