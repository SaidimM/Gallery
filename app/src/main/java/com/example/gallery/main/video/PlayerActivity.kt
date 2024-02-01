package com.example.gallery.main.video

import android.os.Bundle
import com.example.gallery.base.ui.pge.BaseActivity
import com.example.gallery.databinding.ActivityPlayerBinding
import com.example.gallery.main.video.player.listener.IVideoInfo


class PlayerActivity : BaseActivity() {

    override val binding: ActivityPlayerBinding by lazy { ActivityPlayerBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowFullScreen(false)
        val extra = intent.getSerializableExtra("video") ?: return
        binding.playerView.videoInfo = extra as IVideoInfo
        lifecycle.addObserver(binding.playerView)
    }
}