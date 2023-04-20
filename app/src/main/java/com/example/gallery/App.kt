package com.example.gallery

import com.blankj.utilcode.util.Utils
import com.example.gallery.base.BaseApplication
import com.facebook.drawee.backends.pipeline.Fresco

class App : BaseApplication() {
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
        Fresco.initialize(this)
    }
}