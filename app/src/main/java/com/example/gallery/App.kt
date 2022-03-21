package com.example.gallery

import android.app.Application
import com.blankj.utilcode.util.Utils
import com.example.gallery.base.BaseApplication

class App: BaseApplication() {
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
    }
}