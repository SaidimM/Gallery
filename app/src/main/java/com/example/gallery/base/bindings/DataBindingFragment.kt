package com.example.gallery.base.bindings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.forEach
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.example.gallery.R

abstract class DataBindingFragment : Fragment() {
    protected lateinit var activity: AppCompatActivity

    protected abstract val binding: ViewDataBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activity = context as AppCompatActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    open fun isDebug(): Boolean {
        return this.activity.applicationContext
            .applicationInfo != null && this.activity.applicationContext
            .applicationInfo.flags and 2 != 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.binding.unbind()
    }
}