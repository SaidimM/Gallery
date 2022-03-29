package com.example.gallery.player.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import com.example.gallery.R
import com.example.gallery.player.listener.OnVideoControlListener


class VideoErrorView(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0): FrameLayout(context, attributeSet, defStyleAttr) {

    // 正常状态
    val STATUS_NORMAL = 0
    // 普通一场
    val STATUS_VIDEO_DETAIL_ERROR = 1
    // 资源错误
    val STATUS_VIDEO_SRC_ERROR = 2
    // 无WIFI
    val STATUS_UN_WIFI_ERROR = 3
    // 无网络
    val STATUS_NO_NETWORK_ERROR = 4
    private var curStatus = 0
    private val video_error_info: TextView
    private val video_error_retry: Button
    private var onVideoControlListener: OnVideoControlListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.video_controller_error, this)
        video_error_info = findViewById(R.id.video_error_info)
        video_error_retry = findViewById(R.id.video_error_retry)

        video_error_retry.setOnClickListener { onVideoControlListener?.onRetry(curStatus.toString()) }
        hideError()
    }

    fun showError(status: Int) {
        visibility = View.VISIBLE
        if (curStatus == status) return
        curStatus = status
        when (status) {
            STATUS_VIDEO_DETAIL_ERROR -> {
                Log.i("DDD", "showVideoDetailError")
                video_error_info.text = "视频加载失败"
                video_error_retry.text = "点此重试"
            }
            STATUS_VIDEO_SRC_ERROR -> {
                Log.i("DDD", "showVideoSrcError")
                video_error_info.text = "视频加载失败"
                video_error_retry.text = "点此重试"
            }
            STATUS_NO_NETWORK_ERROR -> {
                Log.i("DDD", "showNoNetWorkError")
                video_error_info.text = "网络连接异常，请检查网络设置后重试"
                video_error_retry.text = "重试"
            }
            STATUS_UN_WIFI_ERROR -> {
                Log.i("DDD", "showUnWifiError")
                video_error_info.text = "温馨提示：您正在使用非WiFi网络，播放将产生流量费用"
                video_error_retry.text = "继续播放"
            }
        }
    }

    fun hideError() {
        Log.i("DDD", "hideError")
        visibility = View.GONE
        curStatus = STATUS_NORMAL
    }

    fun setOnVideoControlListener(onVideoControlListener: OnVideoControlListener?) {
        this.onVideoControlListener = onVideoControlListener
    }

    fun getCurStatus(): Int {
        return curStatus
    }
}