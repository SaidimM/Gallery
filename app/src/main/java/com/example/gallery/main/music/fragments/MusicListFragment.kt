package com.example.gallery.main.music.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SnackbarUtils
import com.example.gallery.R
import com.example.gallery.base.ui.pge.BaseFragment
import com.example.gallery.base.ui.pge.BaseRecyclerViewAdapter
import com.example.gallery.base.utils.ViewUtils.loadAlbumCover
import com.example.gallery.databinding.FragmentMusicListBinding
import com.example.gallery.databinding.ItemSongBinding
import com.example.gallery.main.music.viewModels.MusicListViewModel
import com.example.gallery.main.music.viewModels.MusicViewModel
import com.example.gallery.media.music.local.bean.Music

class MusicListFragment : BaseFragment() {
    override val binding: FragmentMusicListBinding by lazy { FragmentMusicListBinding.inflate(layoutInflater) }

    private val viewModel: MusicListViewModel by viewModels()
    private val state: MusicViewModel by lazy { getActivityScopeViewModel(MusicViewModel::class.java) }
    private lateinit var adapter: BaseRecyclerViewAdapter<Music, ItemSongBinding>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initData()
        observe()
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
                    state.play(position)
                    state.saveCurrentMusic()
                }
                binding.mv.visibility = if (item.mvId == 0) View.GONE else View.VISIBLE
                lifecycleScope.launchWhenCreated { loadAlbumCover(item, binding.albumImage) }
            }
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initData() {
        state.loadMusic()
    }

    private fun observe() {
        viewModel.musicVideo.observe(viewLifecycleOwner) {
//            val intent = Intent(requireContext(), PlayerActivity::class.java)
//            val link = it.data.brs.let { br ->
//                br.`1080` ?: br.`720` ?: br.`480` ?: br.`240`
//            }
//            val info = VideoInfo(it.data.name, link!!)
//            intent.putExtra("video", info)
//            startActivity(intent)
        }
        state.musics.observe(viewLifecycleOwner) {
            adapter.data = it
            state.getLastPlayedMusic()
        }
        state.progress.observe(viewLifecycleOwner) {
            SnackbarUtils.with(requireView()).setAction("progress: ${it * 100}%") { }
        }
    }
}