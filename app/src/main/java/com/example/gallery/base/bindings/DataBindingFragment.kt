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
    private lateinit var binding: ViewDataBinding
    private var mTvStrictModeTip: TextView? = null

    protected abstract fun initViewModel()

    protected abstract fun getBindingConfig(): BindingConfig

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activity = context as AppCompatActivity
    }

    protected open fun getBinding(): ViewDataBinding? {
        if (isDebug() && this.mTvStrictModeTip == null) {
            this.mTvStrictModeTip = TextView(this.context)
            this.mTvStrictModeTip!!.alpha = 0.5f
            this.mTvStrictModeTip!!.textSize = 16.0f
            this.mTvStrictModeTip!!.setBackgroundColor(-1)
            this.mTvStrictModeTip!!.setText(R.string.debug_fragment_databinding_warning)
            (this.binding.root as ViewGroup).addView(this.mTvStrictModeTip)
        }
        return this.binding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val bindingConfig = getBindingConfig()
        val binding = DataBindingUtil.setContentView<ViewDataBinding>(activity, bindingConfig.getLayoutId())
        binding.lifecycleOwner = activity
        binding.setVariable(bindingConfig.getViewModelId(), bindingConfig.getViewModel())
        bindingConfig.getBindingParams().forEach { key, value -> binding.setVariable(key, value) }
        return super.onCreateView(inflater, container, savedInstanceState)
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