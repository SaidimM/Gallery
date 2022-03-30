package com.example.gallery.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gallery.R
import com.example.gallery.player.MusicVideoPlayerView
import com.example.gallery.player.VideoInfo

class PlayerActivity : AppCompatActivity() {
    private lateinit var playerView: MusicVideoPlayerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        playerView = findViewById(R.id.player_view)
        val extra = intent.getSerializableExtra("video") as VideoInfo
        playerView.videoInfo = extra
    }
}