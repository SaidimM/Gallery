package com.example.gallery.main.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPUtils
import com.example.gallery.BR
import com.example.gallery.R
import com.example.gallery.Strings
import com.example.gallery.base.bindings.BindingConfig
import com.example.gallery.base.ui.pge.BaseActivity
import com.example.gallery.base.ui.pge.BaseRecyclerViewAdapter
import com.example.gallery.databinding.ItemSongBinding
import com.example.gallery.main.state.MusicViewModel
import com.example.gallery.main.views.player.view.VideoInfo
import com.example.gallery.media.local.Music
import kotlinx.android.synthetic.main.activity_music.*

class MusicActivity : BaseActivity() {
    private lateinit var viewModel: MusicViewModel
    private lateinit var adapter: BaseRecyclerViewAdapter<Music, ItemSongBinding>
    override fun initViewModel() {
        viewModel = getActivityScopeViewModel(MusicViewModel::class.java)
    }

    override fun getBindingConfig() = BindingConfig(R.layout.activity_music, BR.viewModel, viewModel)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRecyclerView()
        observeViewModel()
        val index = SPUtils.getInstance().getInt(Strings.MUSIC_INDEX, 0)
        recyclerView.smoothScrollToPosition(index)
    }

    private fun initRecyclerView() {
        adapter = object : BaseRecyclerViewAdapter<Music, ItemSongBinding>(this) {
            override fun getResourceId(viewType: Int) = R.layout.item_song
            override fun onBindItem(binding: ItemSongBinding, item: Music, position: Int) {
                binding.song = item
                binding.albumImage.background = null
                binding.mv.setOnClickListener {
                    viewModel.getMv(item)
                }
                binding.root.setOnClickListener {
                    SPUtils.getInstance().put(Strings.MUSIC_INDEX, position)
                    viewModel.playMusic(position)
                }
                binding.mv.visibility = if (item.mvId == 0) View.GONE else View.VISIBLE
                viewModel.loadAlbumCover(item, binding.albumImage)
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter.data = viewModel.musics
    }

    private fun observeViewModel() {
        viewModel.musicVideo.observe(this) {
            val intent = Intent(this, PlayerActivity::class.java)
            val link = it.data.brs.let { br ->
                br.`1080` ?: br.`720` ?: br.`480` ?: br.`240`
            }
            val info = VideoInfo(it.data.name, link!!)
            intent.putExtra("video", info)
            startActivity(intent)
        }
    }

}