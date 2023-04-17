package com.example.gallery.main.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.PermissionUtils
import com.example.gallery.BR
import com.example.gallery.R
import com.example.gallery.base.bindings.BindingConfig
import com.example.gallery.base.ui.pge.BaseActivity
import com.example.gallery.main.state.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    private lateinit var viewModel: MainActivityViewModel
    override fun initViewModel() {
        viewModel = getActiityScopeViewModel(MainActivityViewModel::class.java)
    }

    override fun getBindingConfig() = BindingConfig(R.layout.activity_main, BR.viewModel, viewModel)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar.navigationIcon?.setVisible(false, false)
    }

    fun onClick(view: View) {
        if (PermissionUtils.isGranted(android.Manifest.permission_group.STORAGE)) {
            val intent = when (view.id) {
                R.id.music -> Intent(this, MusicActivity::class.java)
                R.id.album -> Intent(this, AlbumActivity::class.java)
                R.id.video -> Intent(this, PlayerActivity::class.java)
                R.id.setting -> Intent(this, SettingActivity::class.java)
                else -> return
            }
            startActivity(intent)
        }
    }
}