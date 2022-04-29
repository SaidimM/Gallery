package com.example.gallery.base.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T, B : ViewDataBinding>(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data: ArrayList<T> = arrayListOf()
        set(value) {
            if (field.isEmpty()) {
                field.addAll(value)
                notifyDataSetChanged()
            } else if (field.size == value.size - 1) {
                field.add(value.last())
                notifyItemInserted(field.size - 1)
            } else {
                return
            }
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
}