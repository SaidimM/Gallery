package com.example.gallery.main.album.adapters

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery.databinding.ItemAlbumImageBinding
import com.example.gallery.databinding.ItemAlbumTitleBinding
import com.example.gallery.main.album.models.AlbumItemModel
import com.example.gallery.main.album.models.AlbumSortModel
import com.example.gallery.media.local.enums.MediaType
import com.example.gallery.media.local.enums.SortType
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AlbumAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data: ArrayList<AlbumItemModel> = arrayListOf()
        set(value) {
            field = value
            data.forEachIndexed { i, _ -> notifyItemChanged(i) }
        }

    var spanCount = 4
        set(value) {
            field = value
            data.forEachIndexed { i, _ -> notifyItemChanged(i) }
        }

    var albumSortModel = AlbumSortModel()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = (context as Activity).layoutInflater
        val binding =
            if (viewType == 1) ItemAlbumTitleBinding.inflate(layoutInflater)
            else ItemAlbumImageBinding.inflate(layoutInflater)
        return BaseViewHolder(binding.root)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]
        if (item.mediaType == MediaType.TITLE) {
            val binding =
                DataBindingUtil.getBinding<ItemAlbumTitleBinding>(holder.itemView) ?: return
            binding.tvTitle.text = when (albumSortModel.sortType) {
                SortType.CREATED -> getDate(item.createdTime)
                SortType.EDITED -> getDate(item.lastEditedTime)
                SortType.ACCESSED -> getDate(item.lastAccessTime)
            }
        } else {
            val binding =
                DataBindingUtil.getBinding<ItemAlbumImageBinding>(holder.itemView) ?: return
            binding.image.layoutParams.apply {
                width = (context as Activity).window.decorView.width / spanCount
                height = width
            }
            setImage(binding.image, item)
            binding.root.setOnClickListener { onItemClickListener(position, item, binding.root) }
        }
    }

    private fun setImage(imageView: SimpleDraweeView, item: AlbumItemModel) {
        val window = (context as Activity).window
        val uri = Uri.parse("file://${item.path}")
        val width = window.decorView.width / spanCount
        val resizeOptions = ResizeOptions(width, width)
        val imageRequest = ImageRequestBuilder
            .newBuilderWithSource(uri)
            .setResizeOptions(resizeOptions)
            .build()
        imageView.controller = Fresco.newDraweeControllerBuilder()
            .setOldController(imageView.controller).setImageRequest(imageRequest)
            .build()
    }

    override fun getItemViewType(position: Int) =
        if (data[position].mediaType == MediaType.TITLE) 1 else 0

    class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    var onItemClickListener: (Int, AlbumItemModel, View) -> Unit = { _, _, _ -> }

    private fun getDate(stamp: Long): String {
        val formatForThisYear = SimpleDateFormat("MMM dd", Locale.US)
        val formatForOtherYear = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        val calendar = Calendar.getInstance()
        calendar.time = Date(stamp)
        val now = Calendar.getInstance()
        return if (calendar[Calendar.YEAR] == now[Calendar.YEAR]) formatForThisYear.format(stamp)
        else formatForOtherYear.format(stamp)
    }
}