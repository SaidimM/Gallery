package com.example.gallery.main.setting

import androidx.activity.viewModels
import androidx.databinding.ViewDataBinding
import com.example.gallery.base.ui.pge.BaseActivity
import com.example.gallery.databinding.ActivitySettingBinding

class SettingActivity : BaseActivity() {
    private val viewModel: SettingViewModel by viewModels()

    override val binding: ViewDataBinding by lazy { ActivitySettingBinding.inflate(layoutInflater) }
}