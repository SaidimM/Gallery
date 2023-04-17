package com.example.gallery.main.activities

import com.example.gallery.BR
import com.example.gallery.R
import com.example.gallery.base.bindings.BindingConfig
import com.example.gallery.base.ui.pge.BaseActivity
import com.example.gallery.main.state.SettingViewModel

class SettingActivity: BaseActivity() {
    private lateinit var viewModel: SettingViewModel
    override fun initViewModel() {
        viewModel = getActiityScopeViewModel(SettingViewModel::class.java)
    }

    override fun getBindingConfig() = BindingConfig(R.layout.activity_setting, BR.viewModel, viewModel)
}