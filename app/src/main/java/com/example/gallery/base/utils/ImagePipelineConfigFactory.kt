package com.example.gallery.base.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Environment
import com.blankj.utilcode.util.Utils
import com.facebook.cache.disk.DiskCacheConfig
import com.facebook.common.util.ByteConstants
import com.facebook.imagepipeline.cache.MemoryCacheParams
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.decoder.ProgressiveJpegConfig
import com.facebook.imagepipeline.image.ImmutableQualityInfo
import com.facebook.imagepipeline.image.QualityInfo
import java.io.File

/**
 * Creates ImagePipeline configuration for the sample app
 */
@SuppressLint("StaticFieldLeak")
object ImagePipelineConfigFactory {
    private const val IMAGE_PIPELINE_CACHE_DIR = "imagepipeline_cache"
    private var sImagePipelineConfig: ImagePipelineConfig? = null

    //    private static ImagePipelineConfig sOkHttpImagePipelineConfig;
    private val MAX_HEAP_SIZE = Runtime.getRuntime().maxMemory().toInt()
    const val MAX_DISK_CACHE_SIZE = 300 * ByteConstants.MB
    val MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 3

    /**
     * Creates config using android http stack as network backend.
     */
    fun getImagePipelineConfig(context: Context): ImagePipelineConfig? {
        if (sImagePipelineConfig == null) {
            val configBuilder = ImagePipelineConfig.newBuilder(context)
            configureCaches(configBuilder, context)
            setProgressive(configBuilder)
            sImagePipelineConfig = configBuilder.build()
        }
        return sImagePipelineConfig
    }

    private fun setProgressive(configBuilder: ImagePipelineConfig.Builder) {
        val progressiveJpegConfig: ProgressiveJpegConfig = object : ProgressiveJpegConfig {
            override fun getNextScanNumberToDecode(scanNumber: Int): Int {
                //返回下一个需要解码的扫描次数
                return scanNumber + 2
            }

            override fun getQualityInfo(scanNumber: Int): QualityInfo {
                val isGoodEnough = scanNumber >= 5
                //确定多少个扫描次数之后的图片才能开始显示。
                return ImmutableQualityInfo.of(scanNumber, isGoodEnough, false)
            }
        }
        //具体含义可参考 http://wiki.jikexueyuan.com/project/fresco/progressive-jpegs.html
        configBuilder.setProgressiveJpegConfig(progressiveJpegConfig)
    }

    /**
     * Configures disk and memory cache not to exceed common limits
     */
    private fun configureCaches(configBuilder: ImagePipelineConfig.Builder, context: Context) {
        val bitmapCacheParams = MemoryCacheParams(
            MAX_MEMORY_CACHE_SIZE, Int.MAX_VALUE,  // Max entries in the cache
            MAX_MEMORY_CACHE_SIZE, Int.MAX_VALUE, Int.MAX_VALUE
        ) // Max cache entry size
        configBuilder
            .setBitmapMemoryCacheParamsSupplier { bitmapCacheParams }
            .setMainDiskCacheConfig(
                DiskCacheConfig.newBuilder(context)
                    .setBaseDirectoryPath(getExternalCacheDir(context))
                    .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR)
                    .setMaxCacheSize(MAX_DISK_CACHE_SIZE.toLong())
                    .build()
            )
    }

    fun getExternalCacheDir(context: Context): File? {
        if (hasExternalCacheDir()) return context.externalCacheDir

        // Before Froyo we need to construct the external cache dir ourselves
        val cacheDir = "/Android/data/" + context.packageName + "/cache/"
        return createFile(Environment.getExternalStorageDirectory().path + cacheDir, "")
    }

    fun hasExternalCacheDir(): Boolean {
        return true
    }

    fun createFile(folderPath: String?, fileName: String?): File {
        val destDir = File(folderPath)
        if (!destDir.exists()) {
            destDir.mkdirs()
        }
        return File(folderPath, fileName)
    }
}