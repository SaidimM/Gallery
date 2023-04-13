package com.example.gallery.main.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPUtils
import com.example.gallery.BR
import com.example.gallery.R
import com.example.gallery.Strings.MUSIC_INDEX
import com.example.gallery.base.bindings.BindingConfig
import com.example.gallery.base.ui.pge.BaseFragment
import com.example.gallery.base.ui.pge.BaseRecyclerViewAdapter
import com.example.gallery.databinding.ItemSongBinding
import com.example.gallery.main.activities.PlayerActivity
import com.example.gallery.main.state.MainActivityViewModel
import com.example.gallery.main.state.MusicFragmentViewModel
import com.example.gallery.media.local.Music
import com.example.gallery.player.view.VideoInfo
import kotlinx.android.synthetic.main.fragment_music.*


class MusicFragment : BaseFragment() {
    private lateinit var viewModel: MusicFragmentViewModel
    private lateinit var state: MainActivityViewModel
    private lateinit var adapter: BaseRecyclerViewAdapter<Music, ItemSongBinding>
    override fun initViewModel() {
        viewModel = getFragmentScopeViewModel(MusicFragmentViewModel::class.java)
        state = getActivityScopeViewModel(MainActivityViewModel::class.java)
    }

    override fun getBindingConfig() = BindingConfig(R.layout.fragment_music, BR.viewModel, viewModel)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        observeViewModel()
        val index = SPUtils.getInstance().getInt(MUSIC_INDEX, 0)
        recyclerView.smoothScrollToPosition(index)
    }

    private fun initRecyclerView() {
        adapter = object : BaseRecyclerViewAdapter<Music, ItemSongBinding>(requireContext()) {
            override fun getResourceId(viewType: Int) = R.layout.item_song
            override fun onBindItem(binding: ItemSongBinding, item: Music, position: Int) {
                binding.song = item
                binding.albumImage.background = null
                binding.mv.setOnClickListener {
                    viewModel.getMv(item)
                }
                binding.root.setOnClickListener {
                    SPUtils.getInstance().put(MUSIC_INDEX, position)
                    state.playMusic(position)
                }
                binding.mv.visibility = if (item.mvId == 0) View.GONE else View.VISIBLE
                state.loadAlbumCover(item, binding.albumImage)
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter.data = state.musics
    }

    private fun observeViewModel() {
        viewModel.musicVideo.observe(viewLifecycleOwner) {
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            val link = it.data.brs.let { br ->
                br.`1080` ?: br.`720` ?: br.`480` ?: br.`240`
            }
            val info = VideoInfo(it.data.name, link!!)
            intent.putExtra("video", info)
            startActivity(intent)
        }
    }
}