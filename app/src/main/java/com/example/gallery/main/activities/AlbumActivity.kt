package com.example.gallery.main.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gallery.base.ui.pge.BaseActivity
import com.example.gallery.base.utils.ImagePipelineConfigFactory
import com.example.gallery.databinding.ActivityAlbumBinding
import com.example.gallery.main.adapters.AlbumAdapter
import com.example.gallery.main.state.AlbumViewModel
import com.example.gallery.media.local.enums.MediaType
import com.facebook.drawee.backends.pipeline.Fresco

class AlbumActivity : BaseActivity() {
    private val viewModel: AlbumViewModel by viewModels()
    private lateinit var adapter: AlbumAdapter
    override val binding: ActivityAlbumBinding by lazy { ActivityAlbumBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this, ImagePipelineConfigFactory.getImagePipelineConfig(this))
        initView()
        initData()
        observe()
    }

    private fun initView() {
        setContentView(binding.root)
        adapter = AlbumAdapter(this)
        binding.recyclerView.adapter = adapter
        val manager = GridLayoutManager(this, viewModel.spamCount.value!!)
        binding.recyclerView.layoutManager = manager
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) =
                if (adapter.getItemViewType(position) == 0) 1 else manager.spanCount
        }
    }

    private fun initData() {
        viewModel.getImages()
    }

    private fun observe() {
        viewModel.album.observe(this) {
            adapter.data = it
        }
        viewModel.spamCount.observe(this) {
            (binding.recyclerView.layoutManager as GridLayoutManager).spanCount = it
            adapter.spanCount = it
        }
    }
}