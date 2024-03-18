package com.example.gallery.main.music.controller

class MusicController {
    companion object {
        @Volatile private var INSTANCE: MusicController? = null
        fun getInstance(): MusicController {
            val controller = INSTANCE
            if (controller != null) return controller
            synchronized(this) {
                val instance = MusicController()
                INSTANCE = instance
                return instance
            }
        }

        fun recycle() {
            INSTANCE = null
        }
    }

    var isPlaying = false

    fun skip(isNext: Boolean = true) {

    }

    fun stopPlaybackService(stopPlayback: Boolean, fromUser: Boolean, fromFocus: Boolean) {
        TODO("Not yet implemented")
    }

    fun repeatSong(int: Int) {
        TODO("Not yet implemented")
    }

    fun resumeOrPause() {
        TODO("Not yet implemented")
    }
}