package com.example.gallery.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.blankj.utilcode.util.PermissionUtils
import com.example.gallery.R
import com.example.gallery.base.ui.pge.BaseActivity
import com.example.gallery.databinding.ActivityMainBinding
import com.example.gallery.main.album.AlbumActivity
import com.example.gallery.main.music.MusicActivity
import com.example.gallery.main.setting.SettingActivity
import com.example.gallery.main.video.PlayerActivity

class MainActivity : BaseActivity() {
    private val viewModel: MainActivityViewModel by viewModels()
    override val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (PermissionUtils.isGranted(android.Manifest.permission_group.STORAGE)) {
            binding.music.visibility = View.VISIBLE
            binding.album.visibility = View.VISIBLE
            binding.video.visibility = View.VISIBLE
        } else {
            binding.music.visibility = View.GONE
            binding.album.visibility = View.GONE
            binding.video.visibility = View.GONE
            super.initPermission()
        }
    }

    fun onClick(view: View) {
        val intent: Intent = when (view.id) {
            R.id.music -> Intent(this, MusicActivity::class.java)
            R.id.album -> Intent(this, AlbumActivity::class.java)
            R.id.video -> Intent(this, PlayerActivity::class.java)
            R.id.setting -> Intent(this, SettingActivity::class.java)
            else -> return
        }
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        binding.music.visibility = View.VISIBLE
        binding.album.visibility = View.VISIBLE
        binding.video.visibility = View.VISIBLE
    }
}