package com.example.gallery.main.activities

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.gallery.R
import com.example.gallery.main.views.player.listener.IVideoInfo
import com.example.gallery.main.views.player.view.VideoPlayerView


class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_player)
        val extra = intent.getSerializableExtra("video") ?: return
        val player_view = findViewById<VideoPlayerView>(R.id.player_view)
        player_view.videoInfo = extra as IVideoInfo
        lifecycle.addObserver(player_view)
    }
}