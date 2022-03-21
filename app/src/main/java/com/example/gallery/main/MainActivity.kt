package com.example.gallery.main

import android.os.Bundle
import android.os.PersistableBundle
import androidx.lifecycle.ViewModel
import com.example.gallery.BR
import com.example.gallery.R
import com.example.gallery.base.bindings.BindingConfig
import com.example.gallery.base.ui.BaseActivity
import com.example.gallery.main.state.MainActivityViewModel

class MainActivity : BaseActivity() {
    private lateinit var viewModel: ViewModel
    override fun initViewModel() {
        viewModel = getActiityScopeViewModel(MainActivityViewModel::class.java)
    }

    override fun getBindingConfig() = BindingConfig(R.layout.activity_main, BR.viewModel, viewModel)

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

    }
}