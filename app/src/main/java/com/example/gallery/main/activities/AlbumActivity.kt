package com.example.gallery.main.activities

import com.example.gallery.BR
import com.example.gallery.R
import com.example.gallery.base.bindings.BindingConfig
import com.example.gallery.base.ui.pge.BaseActivity
import com.example.gallery.main.state.AlbumViewModel

class AlbumActivity : BaseActivity() {
    private lateinit var viewModel: AlbumViewModel
    override fun initViewModel() {
        viewModel = getActivityScopeViewModel(AlbumViewModel::class.java)
    }

    override fun getBindingConfig() = BindingConfig(R.layout.activity_album, BR.viewModel, viewModel)


}