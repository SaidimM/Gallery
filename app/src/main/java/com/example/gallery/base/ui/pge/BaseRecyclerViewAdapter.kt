package com.example.gallery.base.ui.pge

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T, B : ViewDataBinding>(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data: ArrayList<T> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    protected abstract fun getResourceId(viewType: Int): Int

    protected abstract fun onBindItem(binding: B, item: T, position: Int)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = DataBindingUtil.getBinding<B>(holder.itemView)
        val item = data[position]
        if (binding == null) return
        return onBindItem(binding, item, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<B>(LayoutInflater.from(context), getResourceId(viewType),parent, false)
        return BaseViewHolder(binding.root)
    }

    class BaseViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun getItemCount() = data.size

    protected val Int.dp: Int get() = run {
        return toFloat().dp
    }

    protected val Float.dp: Int get() = run {
        val scale: Float = context.resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

    protected val Int.px: Int get() = run {
        return toFloat().px
    }

    protected val Float.px: Int get() = run {
        val scale: Float = context.resources.displayMetrics.density
        return ((this - 0.5f) / scale).toInt()
    }
}