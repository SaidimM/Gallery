package com.example.gallery.player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import com.blankj.utilcode.util.NetworkUtils
import com.example.gallery.R
import com.example.gallery.player.listener.OnPlayerCallback
import com.example.gallery.player.view.VideoBehaviorView
import com.example.gallery.player.view.VideoErrorView.Companion.STATUS_NO_NETWORK_ERROR
import com.example.gallery.player.view.VideoErrorView.Companion.STATUS_UN_WIFI_ERROR
import com.example.gallery.player.view.VideoErrorView.Companion.STATUS_VIDEO_DETAIL_ERROR
import com.example.gallery.player.view.VideoErrorView.Companion.STATUS_VIDEO_SRC_ERROR
import kotlinx.android.synthetic.main.view_player.view.*

class MusicVideoPlayerView(context: Context, attributeSet: AttributeSet? = null, defStyleAttrs: Int = 0) :
    VideoBehaviorView(context, attributeSet, defStyleAttrs) {
    private val tag = this.javaClass.simpleName
    private var player: MyPlayer = MyPlayer()
    private var netChangedReceiver: NetChangedReceiver? = null
    var videoInfo: VideoInfo? = null
        set(value) {
            if (value == null) return
            field = value
            player.reset()
            player.path = value.getPath()
            player.openVideo()
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_player, this)
        surface.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                Log.i(tag, "surface created: ")
                player.holder = holder
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }
        })
        registerNetChangedReceiver()
        initPlayer()
    }

    private fun initPlayer() {
        player.onPlayerListener = object: OnPlayerCallback{
            override fun onperpared(player: MediaPlayer) {
                player.start()
                error_view.hideError()
            }

            override fun onVideoSizeChanged(mp: MediaPlayer?, width: Int, height: Int) {
            }

            override fun onBufferingUpdate(mediaPlayer: MediaPlayer, percent: Int) {
            }

            override fun onCompletion(mediaPlayer: MediaPlayer) {
                controller_view.isPlaying = false
            }

            override fun onError(mediaPlayer: MediaPlayer, what: Int, extra: Int) {
                checkShowError(false)
            }

            override fun onLoadingChanged(isChanged: Boolean) {
                loading_view.visibility = if (isChanged) VISIBLE else GONE
            }

            override fun onStateChanged(state: Int) {
                when (state) {
                    MyPlayer.STATE_IDLE -> audioManager.abandonAudioFocus(null)
                    MyPlayer.STATE_PREPARING -> audioManager.requestAudioFocus(
                        null,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN
                    )
                }
            }
        }
    }

    private fun registerNetChangedReceiver() {
        if (netChangedReceiver == null) {
            netChangedReceiver = NetChangedReceiver()
            val filter = IntentFilter()
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
            context.registerReceiver(netChangedReceiver, filter)
        }
    }

    override fun endGesture(behaviorType: Int) {
        if (behaviorType == FINGER_BEHAVIOR_PROGRESS) {
            player.seekTo(progress_view.getTargetProgress())
            system_view.hide()
        } else progress_view.hide()
    }

    override fun updateSeek(defProgress: Int) {
        progress_view.updateProgress(defProgress, player.getCurrentPosition(), player.getDuration())
    }

    override fun updateVolume(max: Int, progress: Int) {
        system_view.updateVolume(max, progress)
    }

    override fun updateBrightness(max: Int, progress: Int) {
        system_view.updateBrightness(max, progress)
    }

    fun checkShowError(isNetChanged: Boolean) {
        val isConnect: Boolean = NetworkUtils.isConnected()
        val isMobileNet: Boolean = NetworkUtils.isMobileData()
        val isWifiNet = NetworkUtils.isWifiAvailable()
        if (isConnect) {
            // 如果已经联网
            if (error_view.getCurStatus() == STATUS_NO_NETWORK_ERROR && !(isMobileNet && !isWifiNet)) {
                // 如果之前是无网络
                error_view.visibility = GONE
            } else if (videoInfo == null) {
                // 优先判断是否有video数据
                showError(STATUS_VIDEO_DETAIL_ERROR)
            } else if (isMobileNet && !isWifiNet) {
                // 如果是手机流量，且未同意过播放，且非本地视频，则提示错误
                error_view.showError(STATUS_UN_WIFI_ERROR)
                player.pause()
            } else if (isWifiNet && isNetChanged && error_view.getCurStatus() == STATUS_UN_WIFI_ERROR) {
                // 如果是wifi流量，且之前是非wifi错误，则恢复播放
//                playFromUnWifiError()
            } else if (!isNetChanged) {
                showError(STATUS_VIDEO_SRC_ERROR)
            }
        } else {
            // 无网，暂停播放并提示
            player.pause()
            showError(STATUS_NO_NETWORK_ERROR)
        }
    }

    private fun showError(state: Int) {
        error_view.showError(state)
        controller_view.hide()
    }

    private class NetChangedReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val extra: NetworkInfo =
                intent?.getParcelableExtra<NetworkInfo>(ConnectivityManager.EXTRA_NETWORK_INFO) ?: return
            if (NetworkUtils.isConnected() && extra.state != NetworkInfo.State.CONNECTED) return
        }
    }

}