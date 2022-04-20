package com.example.gallery.main

import android.Manifest
import android.app.WallpaperManager
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.gallery.BR
import com.example.gallery.R
import com.example.gallery.base.bindings.BindingConfig
import com.example.gallery.base.ui.BaseActivity
import com.example.gallery.main.state.MainActivityViewModel

class MainActivity : BaseActivity() {
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var navController: NavController
    override fun initViewModel() {
        viewModel = getActiityScopeViewModel(MainActivityViewModel::class.java)
    }

    override fun getBindingConfig() = BindingConfig(R.layout.activity_main, BR.viewModel, viewModel)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.fitsSystemWindows = true
        navController = (supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment).navController
        observe()
    }

    fun toMusic(view: View) {
        view.setBackgroundColor(getColor(R.color.gray_e5))
        navController.navigate(R.id.action_mainFragment_to_musicFragment)
    }

    private fun observe() {
        viewModel.index.observe(this) {
            navController.navigate(it)
        }
    }
}