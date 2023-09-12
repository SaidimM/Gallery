package com.example.gallery.main.music.adapters

import LogUtil
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.children
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery.R
import com.example.gallery.base.ui.pge.BaseRecyclerViewAdapter
import com.example.gallery.databinding.ItemLyricBinding
import com.example.gallery.media.remote.lyrics.Lyric
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class LyricsAdapter(
    private val recyclerView: RecyclerView
) : BaseRecyclerViewAdapter<Lyric, ItemLyricBinding>(recyclerView.context) {

    private val TAG = "LyricsAdapter"
    private var index = 0
    private var curDuration = 0
    override fun getResourceId(viewType: Int) = R.layout.item_lyric

    override fun onBindItem(binding: ItemLyricBinding, item: Lyric, position: Int) {
        binding.lyric = item.text
    }

    @OptIn(FlowPreview::class)
    suspend fun start() {
        data.asFlow()
            .flatMapConcat {
                flow {
                    delay(it.endPosition.toLong() - it.position.toLong())
                    emit(it)
                }
            }.transform {
                emit(data.indexOf(it))
            }.collect {
                index = it
                coroutineScope {
                    LogUtil.d(TAG, "index $index")
                    launch(Dispatchers.Main) {
                        onScroll()
                    }
                }
            }
    }

    private fun onScroll() {
        if (recyclerView.children.count() <= index) return
        val start = recyclerView.scrollY.px
        val height = recyclerView.get(index).height.px + 16
        LogUtil.d(TAG, "view height: $height")
        ObjectAnimator.ofInt(start, start + height).apply {
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { recyclerView.smoothScrollBy(0, it.animatedValue as Int) }
            start()
        }
    }
}