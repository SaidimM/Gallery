package com.example.gallery.main.activities

import androidx.activity.viewModels
import androidx.databinding.ViewDataBinding
import com.example.gallery.base.ui.pge.BaseActivity
import com.example.gallery.main.state.SettingViewModel

class SettingActivity : BaseActivity() {
    private val viewModel: SettingViewModel by viewModels()

    override val binding: ViewDataBinding
        get() = TODO("Not yet implemented")
}