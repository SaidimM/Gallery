package com.example.gallery.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gallery.R
import kotlinx.android.synthetic.main.activity_player.*


class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        val extra = intent.getStringExtra("video")
        player_view.path = extra.toString()
    }
}