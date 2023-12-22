package com.example.gallery

import com.blankj.utilcode.util.Utils
import com.example.gallery.base.BaseApplication
import com.facebook.cache.disk.DiskCacheConfig
import com.facebook.common.util.ByteConstants
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.cache.MemoryCacheParams
import com.facebook.imagepipeline.core.ImagePipelineConfig
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService


class App : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        initFresco()
        Utils.init(this)
        // SQLiteStudio Service
        SQLiteStudioService.instance().setPort(19980)
        SQLiteStudioService.instance().start(this)
    }

    companion object {
        private val MAX_HEAP_SIZE = Runtime.getRuntime().maxMemory().toInt()
        private val MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 4
        private const val MAX_DISK_CACHE_SIZE = 40L * ByteConstants.MB
    }

    private fun initFresco() {
        val config = ImagePipelineConfig
            .newBuilder(this)
            .setBitmapMemoryCacheParamsSupplier {
                MemoryCacheParams(
                    MAX_MEMORY_CACHE_SIZE,
                    Int.MAX_VALUE,
                    MAX_MEMORY_CACHE_SIZE,
                    Int.MAX_VALUE,
                    Int.MAX_VALUE
                )
            }.setMainDiskCacheConfig(
                DiskCacheConfig.newBuilder(this)
                    .setBaseDirectoryPath(cacheDir)
                    .setMaxCacheSize(MAX_DISK_CACHE_SIZE)
                    .build()
            ).setDownsampleEnabled(true)
            .build()
        Fresco.initialize(this, config)
    }
}