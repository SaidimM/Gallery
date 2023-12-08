package com.example.gallery.main.album

import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gallery.R
import com.example.gallery.base.ui.pge.BaseActivity
import com.example.gallery.base.utils.ImagePipelineConfigFactory
import com.example.gallery.databinding.ActivityAlbumBinding
import com.example.gallery.databinding.DialogAlbumSortBinding
import com.example.gallery.main.album.adapters.AlbumAdapter
import com.example.gallery.main.album.fragments.PreviewFragment
import com.example.gallery.main.album.models.AlbumItemModel
import com.example.gallery.main.album.viewModels.AlbumViewModel
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.material.bottomsheet.BottomSheetDialog

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
        title = getString(R.string.album)
        adapter = AlbumAdapter(this)
        adapter.albumSortModel = viewModel.sortModel
        binding.recyclerView.adapter = adapter
        val manager = GridLayoutManager(this, viewModel.spamCount.value!!)
        binding.recyclerView.layoutManager = manager
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) =
                if (adapter.getItemViewType(position) == 0) 1 else manager.spanCount
        }
        adapter.onItemClickListener = { position, index, view -> displayPreview(adapter.data[position], view) }
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.toolbar.navigationIcon = AppCompatResources.getDrawable(this, R.drawable.ic_back)
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

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialog).apply {
            setCancelable(true)
            val binding = DialogAlbumSortBinding.inflate(layoutInflater)
            setContentView(binding.root)
            binding.sortModel = viewModel.sortModel
            binding.cancelButton.setOnClickListener { cancel() }
            binding.confirmButton.setOnClickListener {
                viewModel.getImages()
                cancel()
            }
        }
        bottomSheetDialog.show()
    }

    private fun displayPreview(imageItem: AlbumItemModel, view: View) {
        val frame = FrameLayout(this).apply { id = R.id.layout }
        binding.constraintLayout.addView(frame, MATCH_PARENT, MATCH_PARENT)
        val fragment = PreviewFragment(imageItem)
        supportFragmentManager.beginTransaction().add(R.id.layout, fragment).commit()
        fragment.onClickListener = { supportFragmentManager.beginTransaction().remove(fragment).commit() }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.fragments.isEmpty()) super.onBackPressed()
        else supportFragmentManager.beginTransaction().remove(supportFragmentManager.fragments.first()).commit()
    }
}