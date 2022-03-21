package com.example.gallery.base.bindings

import android.os.Bundle
import android.os.PersistableBundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.forEach
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.gallery.R

abstract class DataBindingActivity: AppCompatActivity() {
    private lateinit var binding: ViewDataBinding
    private var mTvStrictModeTip: TextView? = null

    protected open fun getBinding(): ViewDataBinding? {
        if (isDebug() && mTvStrictModeTip == null) {
            mTvStrictModeTip = TextView(this.applicationContext)
            mTvStrictModeTip!!.alpha = 0.4f
            mTvStrictModeTip!!.textSize = 14.0f
            mTvStrictModeTip!!.setBackgroundColor(-1)
            mTvStrictModeTip!!.setText(R.string.debug_activity_databinding_warning)
            (this.binding.root as ViewGroup).addView(mTvStrictModeTip)
        }
        return this.binding
    }

    protected abstract fun initViewModel()

    protected abstract fun getBindingConfig(): BindingConfig

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        initViewModel()
        val bindingConfig: BindingConfig = getBindingConfig()
        val binding = DataBindingUtil.setContentView<ViewDataBinding>(this, bindingConfig.getLayoutId())
        binding.lifecycleOwner = this
        binding.setVariable(bindingConfig.getViewModelId(), bindingConfig.getViewModel())
        bindingConfig.getBindingParams().forEach { key, value -> binding.setVariable(key, value) }
        this.binding = binding
    }

    open fun isDebug(): Boolean {
        return this.applicationContext.applicationInfo != null && this.applicationContext.applicationInfo.flags and 2 != 0
    }

    override fun onDestroy() {
        super.onDestroy()
        this.binding.unbind()
    }
}