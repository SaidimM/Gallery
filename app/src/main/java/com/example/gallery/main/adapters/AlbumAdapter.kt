package com.example.gallery.main.adapters

import android.app.Activity
import android.content.Context
import android.net.Uri
import com.example.gallery.R
import com.example.gallery.base.ui.pge.BaseRecyclerViewAdapter
import com.example.gallery.databinding.ItemImageBinding
import com.example.gallery.main.model.AlbumItemModel
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import org.jetbrains.anko.collections.forEachWithIndex

class AlbumAdapter(private val context: Context): BaseRecyclerViewAdapter<AlbumItemModel, ItemImageBinding>(context) {
    var spamCount = 4
        set(value) {
            field = value
            data.forEachWithIndex { i, _ -> notifyItemChanged(i) }
        }
    override fun getResourceId(viewType: Int) = R.layout.item_image

    override fun onBindItem(binding: ItemImageBinding, item: AlbumItemModel, position: Int) {
        val window = (context as Activity).window
        binding.root.layoutParams.apply { height = window.decorView.width / spamCount }
        setImage(binding.image, item)
    }

    private fun setImage(imageView: SimpleDraweeView, item: AlbumItemModel) {
        val window = (context as Activity).window
        val uri = Uri.parse("file://${item.path}")
        val width = window.decorView.width / spamCount
        val resizeOptions = ResizeOptions(width, width)
        val imageRequest = ImageRequestBuilder.newBuilderWithSource(uri).setResizeOptions(resizeOptions).build()
        imageView.controller =
            Fresco.newDraweeControllerBuilder().setOldController(imageView.controller).setImageRequest(imageRequest)
                .build()
    }
}