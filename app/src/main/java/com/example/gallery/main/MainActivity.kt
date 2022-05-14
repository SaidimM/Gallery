package com.example.gallery.main

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import com.blankj.utilcode.util.PermissionUtils
import com.example.gallery.BR
import com.example.gallery.R
import com.example.gallery.base.bindings.BindingConfig
import com.example.gallery.base.ui.navigation.NavHostFragment
import com.example.gallery.base.ui.pge.BaseActivity
import com.example.gallery.main.state.MainActivityViewModel
import com.example.gallery.media.local.Music

class MainActivity : BaseActivity() {
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var navController: NavController
    override fun initViewModel() {
        viewModel = getActiityScopeViewModel(MainActivityViewModel::class.java)
    }

    override fun getBindingConfig() = BindingConfig(R.layout.activity_main, BR.viewModel, viewModel)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = (supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment).navController
        observe()
        if (PermissionUtils.isGranted(android.Manifest.permission_group.STORAGE)) viewModel.loadMusic()
        else super.initPermission()
    }

    fun toMusic(view: View) {
        view.setBackgroundColor(getColor(R.color.gray_e5))
        navController.navigate(R.id.action_mainFragment_to_musicFragment)
        viewModel.index = R.id.musicFragment
    }

    fun toLyrics(music: Music) {
        navController.navigate(R.id.action_musicFragment_to_lyricsFragment)
        viewModel.toLyric(music)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        viewModel.loadMusic()
    }

    private fun observe() {

    }
}