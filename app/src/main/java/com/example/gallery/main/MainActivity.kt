package com.example.gallery.main

import LogUtil
import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.example.gallery.base.ui.pge.BaseActivity
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

    fun grantPermissions(view: View) = initPermission(permissions)

    override fun observe() {
        viewModel.createDirectories()
        viewModel.isPermissionGranted.observe(this) {
            LogUtil.d(TAG, "isPermissionGranted: $it")
            binding.greeting.visibility = if (it) View.GONE else View.VISIBLE
            binding.linearLayout.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.contains(-1)) viewModel.permissionGranted(false)
        else viewModel.permissionGranted(true)
    }

    override fun onDestroy() {
        Fresco.getImagePipeline().clearCaches()
        super.onDestroy()
    }
}