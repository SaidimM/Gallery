package com.example.gallery.main.fragments

import com.example.gallery.BR
import com.example.gallery.R
import com.example.gallery.base.bindings.BindingConfig
import com.example.gallery.base.ui.pge.BaseFragment
import com.example.gallery.main.activities.MainActivity
import com.example.gallery.main.state.MainActivityViewModel
import com.example.gallery.main.state.MainFragmentViewModel

class MainFragment : BaseFragment() {
    private lateinit var viewModel: MainFragmentViewModel
    private lateinit var state: MainActivityViewModel
    override fun initViewModel() {
        viewModel = getFragmentScopeViewModel(MainFragmentViewModel::class.java)
        state = getActivityScopeViewModel(MainActivityViewModel::class.java)
    }

    override fun getBindingConfig() = BindingConfig(R.layout.fragment_main, BR.viewModel, viewModel)
}