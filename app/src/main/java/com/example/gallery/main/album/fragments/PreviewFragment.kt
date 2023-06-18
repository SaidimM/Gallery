package com.example.gallery.main.album.fragments

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.viewModels
import com.example.gallery.base.ui.pge.BaseFragment
import com.example.gallery.databinding.FragmentPreviewBinding
import com.example.gallery.main.album.models.AlbumItemModel
import com.example.gallery.main.album.viewModels.PreviewFragmentViewModel

class PreviewFragment(private val imageItem: AlbumItemModel) : BaseFragment() {
    private val viewModel: PreviewFragmentViewModel by viewModels()

    override val binding: FragmentPreviewBinding by lazy { FragmentPreviewBinding.inflate(layoutInflater) }

    var onClickListener: () -> Unit = {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.imageItem = imageItem
        initView()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    override fun onPause() {
        super.onPause()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    private fun initView() {
        binding.image.setImageURI("file://${imageItem.path}")
    }
}