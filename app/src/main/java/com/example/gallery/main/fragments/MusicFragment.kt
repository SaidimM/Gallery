package com.example.gallery.main.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.gallery.BR
import com.example.gallery.R
import com.example.gallery.base.bindings.BindingConfig
import com.example.gallery.base.ui.BaseFragment
import com.example.gallery.base.ui.BaseRecyclerViewAdapter
import com.example.gallery.databinding.ItemSongBinding
import com.example.gallery.main.MainActivity
import com.example.gallery.main.PlayerActivity
import com.example.gallery.main.state.MainActivityViewModel
import com.example.gallery.main.state.MusicFragmentViewModel
import com.example.gallery.media.local.Music
import com.example.gallery.player.VideoInfo
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
        observeVideModel()
    }

    private fun initRecyclerView() {
        adapter = object : BaseRecyclerViewAdapter<Music, ItemSongBinding>(requireContext()) {
            override fun getResourceId(viewType: Int) = R.layout.item_song
            override fun onBindItem(binding: ItemSongBinding, item: Music, position: Int) {
                binding.song = item
                binding.mv.setOnClickListener {
                    viewModel.getMv(item)
                }
                val bitmap = viewModel.getArtistImage(item)
                Glide.with(requireContext()).load(bitmap).into(binding.albumImage)
                binding.root.setOnClickListener {
                    if (bitmap == null) state.saveAlbumImage(item)
                    (context as MainActivity).toLyrics(item)
                }
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeVideModel() {
        state.songs.observe(viewLifecycleOwner) {
            if (it.isEmpty()) return@observe
            adapter.data = it
        }
        viewModel.musicVideo.observe(viewLifecycleOwner) {
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            val link = it.data.brs.let { br->
                br.`1080` ?: br.`720` ?: br.`480` ?: br.`240`
            }
            val info = VideoInfo(it.data.name, link!!)
            intent.putExtra("video", info)
            startActivity(intent)
        }
    }
}