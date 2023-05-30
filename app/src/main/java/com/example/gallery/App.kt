package com.example.gallery

import com.blankj.utilcode.util.Utils
import com.example.gallery.base.BaseApplication
import com.example.gallery.base.utils.ImagePipelineConfigFactory
import com.facebook.drawee.backends.pipeline.Fresco
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService


class App : BaseApplication() {
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
        Fresco.initialize(this, ImagePipelineConfigFactory.getImagePipelineConfig(this))

        // SQLiteStudio Service
        SQLiteStudioService.instance().setPort(20009)
        SQLiteStudioService.instance().start(this)
    }
}