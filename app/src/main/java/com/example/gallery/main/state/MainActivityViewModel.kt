package com.example.gallery.main.state

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import com.blankj.utilcode.util.Utils
import com.bumptech.glide.Glide
import com.example.gallery.Strings
import com.example.gallery.Strings.ALBUM_COVER_DIR
import com.example.gallery.base.utils.LocalMusicUtils
import com.example.gallery.base.utils.LocalMusicUtils.bitmapToFile
import com.example.gallery.base.utils.blurHash.BlurHash
import com.example.gallery.media.MusicRepository
import com.example.gallery.media.local.Music
import com.example.gallery.media.local.MusicDatabase
import com.example.gallery.main.views.player.controller.MusicPlayer
import com.example.gallery.main.views.player.state.PlayState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File

class MainActivityViewModel : ViewModel() {
}