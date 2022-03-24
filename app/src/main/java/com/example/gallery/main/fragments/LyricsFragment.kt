package com.example.gallery.main.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gallery.BR
import com.example.gallery.R
import com.example.gallery.base.bindings.BindingConfig
import com.example.gallery.base.ui.BaseFragment
import com.example.gallery.base.ui.BaseRecyclerViewAdapter
import com.example.gallery.databinding.ItemLyricBinding
import com.example.gallery.main.state.LyricsFragmentViewModel
import com.example.gallery.main.state.MainActivityViewModel
import com.example.gallery.media.MediaViewModel
import com.example.gallery.media.remote.lyrics.Lyric
import kotlinx.android.synthetic.main.fragment_music.*

class LyricsFragment: BaseFragment() {
    private lateinit var viewModel: LyricsFragmentViewModel
    private lateinit var state: MainActivityViewModel
    private lateinit var media: MediaViewModel

    private lateinit var adapter: BaseRecyclerViewAdapter<Lyric, ItemLyricBinding>
    override fun getBindingConfig() = BindingConfig(R.layout.fragment_lyrics, BR.viewModel, viewModel)
    override fun initViewModel() {
        viewModel = getFragmentScopeViewModel(LyricsFragmentViewModel::class.java)
        state = getActivityScopeViewModel(MainActivityViewModel::class.java)
        media = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(activity.application)
            .create(MediaViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (state.music.value == null) return
        initRecyclerView()
        viewModel.getLyric(state.music.value!!)
        observe()
    }

    private fun initRecyclerView() {
        adapter = object: BaseRecyclerViewAdapter<Lyric, ItemLyricBinding>(requireContext()){
            override fun getResourceId(viewType: Int) = R.layout.item_lyric
            override fun onBindItem(binding: ItemLyricBinding, item: Lyric, position: Int) {
                binding.text.text = ""
                binding.text.text = item.text
            }
        }
        recyclerView.adapter = adapter
    }

    private fun observe() {
        viewModel.lyrics.observe(viewLifecycleOwner) {
            adapter.data = it
        }
    }
}