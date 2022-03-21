package com.example.gallery.base.bindings

import android.util.SparseArray
import androidx.lifecycle.ViewModel

class BindingConfig constructor(
    private val layoutId: Int,
    private val viewModelId: Int,
    private val viewModel: ViewModel
) {
    private val bindingParams: SparseArray<Any> = SparseArray()

    fun addBindingParams(id: Int, param: Any): BindingConfig {
        if (bindingParams[id] == null) bindingParams[id] = param
        return this
    }

    fun getViewModelId() = viewModelId

    fun getLayoutId() = layoutId

    fun getViewModel() = viewModel

    fun getBindingParams() = bindingParams
}