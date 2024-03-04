package com.example.gallery.main

import android.Manifest
import android.content.Intent
import android.icu.number.Scale
import android.os.Bundle
import android.view.View
import android.view.animation.ScaleAnimation
import androidx.activity.viewModels
import com.example.gallery.base.ui.pge.BaseActivity
import com.example.gallery.base.utils.ViewUtils.setHeight
import com.example.gallery.databinding.ActivityMainBinding
import com.example.gallery.main.album.AlbumActivity
import com.example.gallery.main.music.MusicActivity
import com.example.gallery.main.setting.SettingActivity
import com.facebook.drawee.backends.pipeline.Fresco

class MainActivity : BaseActivity() {
    private val viewModel: MainActivityViewModel by viewModels()
    override val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        if (!isPermissionsGranted(permissions)) initPermission(permissions)
        else viewModel.permissionGranted(true)
    }

    fun toAlbum(view: View) = startActivity(Intent(this, AlbumActivity::class.java))

    fun toMusic(view: View) = startActivity(Intent(this, MusicActivity::class.java))

    fun toVideo(view: View) = startActivity(Intent(this, MusicActivity::class.java))

    fun toSetting(view: View) = startActivity(Intent(this, SettingActivity::class.java))

    override fun observe() {
        viewModel.createDirectories()
        viewModel.isPermissionGranted.observe(this) {
            binding.music.visibility = if (it) View.VISIBLE else View.GONE
            binding.album.visibility = if (it) View.VISIBLE else View.GONE
            binding.video.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        viewModel.permissionGranted(true)
    }

    override fun onDestroy() {
        Fresco.getImagePipeline().clearCaches()
        super.onDestroy()
    }
}