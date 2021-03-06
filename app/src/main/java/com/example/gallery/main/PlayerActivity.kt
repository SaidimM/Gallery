package com.example.gallery.main

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.gallery.R
import com.example.gallery.player.listener.IVideoInfo
import kotlinx.android.synthetic.main.activity_player.*


class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_player)
        val extra = intent.getSerializableExtra("video") ?: return
        player_view.videoInfo = extra as IVideoInfo
        lifecycle.addObserver(player_view)
    }
}