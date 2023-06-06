package com.example.gallery.main.fragments

import androidx.fragment.app.viewModels
import com.example.gallery.base.ui.pge.BaseFragment
import com.example.gallery.databinding.FragmentMainBinding
import com.example.gallery.main.state.MainActivityViewModel
import com.example.gallery.main.state.MainFragmentViewModel

class MainFragment : BaseFragment() {
    private val viewModel: MainFragmentViewModel by viewModels()
    private val state: MainActivityViewModel by viewModels()

    override val binding: FragmentMainBinding by lazy { FragmentMainBinding.inflate(layoutInflater) }
}