package com.example.gallery.main.activities

import android.os.Bundle
import com.example.gallery.BR
import com.example.gallery.R
import com.example.gallery.base.bindings.BindingConfig
import com.example.gallery.base.ui.pge.BaseActivity
import com.example.gallery.base.ui.pge.BaseRecyclerViewAdapter
import com.example.gallery.databinding.ItemImageBinding
import com.example.gallery.main.model.AlbumItemModel
import com.example.gallery.main.state.AlbumViewModel

class AlbumActivity : BaseActivity() {
    private lateinit var viewModel: AlbumViewModel
    private lateinit var adapter: BaseRecyclerViewAdapter<AlbumItemModel, ItemImageBinding>

    override fun initViewModel() {
        viewModel = getActiityScopeViewModel(AlbumViewModel::class.java)
    }

    override fun getBindingConfig() = BindingConfig(R.layout.activity_album, BR.viewModel, viewModel)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        viewModel.getImages()
        adapter = object : BaseRecyclerViewAdapter<AlbumItemModel, ItemImageBinding>(this) {
            override fun getResourceId(viewType: Int) = R.layout.item_image

            override fun onBindItem(binding: ItemImageBinding, item: AlbumItemModel, position: Int) {
                binding.image.setImageURI("file://" + item.path)
            }
        }
    }
}