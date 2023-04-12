package com.example.gallery.main

import android.animation.ValueAnimator
import android.graphics.Interpolator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.blankj.utilcode.util.PermissionUtils
import com.bumptech.glide.Glide
import com.example.gallery.BR
import com.example.gallery.R
import com.example.gallery.base.bindings.BindingConfig
import com.example.gallery.base.ui.pge.BaseActivity
import com.example.gallery.main.state.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*

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
        if (PermissionUtils.isGranted(android.Manifest.permission_group.STORAGE)) viewModel.loadMusic()
        else super.initPermission()
        toolbar.navigationIcon?.setVisible(false, false)
        observeViewModel()
    }

    fun toMusic(view: View) {
        toolbar.apply {
            navigationIcon?.setVisible(true, false)
            setNavigationOnClickListener { onBackPressed() }
            title = getString(R.string.music_library)
        }
        view.setBackgroundColor(getColor(R.color.gray_e5))
        navController.navigate(R.id.action_mainFragment_to_musicFragment)
        viewModel.index = R.id.musicFragment
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        viewModel.loadMusic()
    }

    private fun observeViewModel() {
        viewModel.music.observe(this) {
            if (player_layout.marginBottom != 0) {
                val animator: ValueAnimator = ValueAnimator().apply {
                    floatArrayOf(player_layout.marginBottom.toFloat(), 0F)
                    duration = 1000
                    interpolator = DecelerateInterpolator()
                    addUpdateListener {
                        val layoutParams: ConstraintLayout.LayoutParams = player_layout.layoutParams as ConstraintLayout.LayoutParams
                        layoutParams.setMargins(0, 0, 0, (it.animatedValue as Float).toInt())
                        player_layout.layoutParams = layoutParams
                    }
                }
                animator.start()
            }
        }
    }
}