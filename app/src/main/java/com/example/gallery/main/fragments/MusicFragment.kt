package com.example.gallery.main.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.gallery.BR
import com.example.gallery.R
import com.example.gallery.base.bindings.BindingConfig
import com.example.gallery.base.ui.BaseFragment
import com.example.gallery.base.ui.BaseRecyclerViewAdapter
import com.example.gallery.databinding.ItemSongBinding
import com.example.gallery.main.PlayerActivity
import com.example.gallery.main.state.MainActivityViewModel
import com.example.gallery.main.state.MusicFragmentViewModel
import com.example.gallery.media.MediaViewModel
import com.example.gallery.media.local.Music
import com.example.gallery.player.VideoInfo
import kotlinx.android.synthetic.main.fragment_music.*

class MusicFragment : BaseFragment() {
    private lateinit var viewModel: MusicFragmentViewModel
    private lateinit var state: MainActivityViewModel
    private lateinit var adapter: BaseRecyclerViewAdapter<Music, ItemSongBinding>
    private lateinit var mediaViewModel: MediaViewModel
    override fun initViewModel() {
        viewModel = getFragmentScopeViewModel(MusicFragmentViewModel::class.java)
        state = getActivityScopeViewModel(MainActivityViewModel::class.java)
        mediaViewModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(activity.application)
            .create(MediaViewModel::class.java)
    }

    override fun getBindingConfig() = BindingConfig(R.layout.fragment_music, BR.viewModel, viewModel)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        observe()
    }

    private fun initRecyclerView() {
        adapter = object : BaseRecyclerViewAdapter<Music, ItemSongBinding>(requireContext()) {
            override fun getResourceId(viewType: Int) = R.layout.item_song
            override fun onBindItem(binding: ItemSongBinding, item: Music, position: Int) {
                binding.song = item
                binding.mv.setOnClickListener {
                    mediaViewModel.getMv(item) {
                        val intent = Intent(requireContext(), PlayerActivity::class.java)
                        val info = VideoInfo(item.name.toString(), it.data.brs.`720`)
                        intent.putExtra("video", info)
                        startActivity(intent)
                    }
                }
                val bitmap = viewModel.getArtistImage(item)
                Glide.with(this@MusicFragment).load(bitmap).into(binding.albumImage)
                binding.root.setOnClickListener {
                    mediaViewModel.getMusicInfo(item)
                    state.toLyric(item)
                }
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.loadMusic()
    }

    private fun observe() {
        viewModel.songs.observe(viewLifecycleOwner) {
            adapter.data = it
        }
    }
}