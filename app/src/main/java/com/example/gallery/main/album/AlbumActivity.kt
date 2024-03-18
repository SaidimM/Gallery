package com.example.gallery.main.album

import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.gallery.R
import com.example.gallery.base.ui.pge.BaseActivity
import com.example.gallery.base.utils.AnimationUtils.onAnimationEnd
import com.example.gallery.base.utils.AnimationUtils.onAnimationStart
import com.example.gallery.databinding.ActivityAlbumBinding
import com.example.gallery.main.album.adapters.AlbumAdapter
import com.example.gallery.main.album.fragments.PreviewFragment
import com.example.gallery.main.album.models.AlbumItemModel
import com.example.gallery.main.album.viewModels.AlbumViewModel

class AlbumActivity : BaseActivity() {

    private val viewModel: AlbumViewModel by viewModels()
    private lateinit var adapter: AlbumAdapter
    override val binding: ActivityAlbumBinding by lazy { ActivityAlbumBinding.inflate(layoutInflater) }

    private val fragmentAdapter = object : FragmentStateAdapter(this) {
        override fun getItemCount() = viewModel.allImages.size
        override fun createFragment(position: Int) = PreviewFragment(viewModel.allImages[position])
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
        observe()
    }

    private fun initView() {
        setWindowFullScreen(true)
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
        adapter.onItemClickListener = { _, item, _ -> displayPreview(item) }
    }

    private fun initData() {
        viewModel.getImages()
    }

    override fun observe() {
        viewModel.album.observe(this) { adapter.data = it }
    }

    private fun displayPreview(item: AlbumItemModel) {
        setWindowFullScreen(false)
        if (binding.viewPager.adapter == null) binding.viewPager.adapter = fragmentAdapter
        binding.viewPager.currentItem = viewModel.allImages.indexOf(item)
        val animation = AlphaAnimation(0f, 1f).apply { duration = 500 }
        animation.onAnimationStart { binding.viewPager.visibility = View.VISIBLE }
        binding.viewPager.startAnimation(animation)
    }

    override fun onBackPressed() {
        if (binding.viewPager.visibility == View.VISIBLE) {
            setWindowFullScreen(true)
            val animation = AlphaAnimation(1f, 0f)
            animation.duration = 200
            animation.onAnimationEnd { binding.viewPager.visibility = View.GONE }
            binding.viewPager.startAnimation(animation)
        } else super.onBackPressed()
    }
}