package com.example.gallery.base.utils

import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener

object AnimationUtils {
    fun Animation.setListeners(onStart: (() -> Unit)? = null, onEnd: (() -> Unit)? = null, onRepeat: (() -> Unit)? = null) {
        this.setAnimationListener(object: AnimationListener{
            override fun onAnimationStart(animation: Animation?) {
                if (onStart != null) {
                    onStart()
                }
            }

            override fun onAnimationEnd(animation: Animation?) {
                if (onEnd != null) {
                    onEnd()
                }
            }

            override fun onAnimationRepeat(animation: Animation?) {
                if (onRepeat != null) {
                    onRepeat()
                }
            }
        })
    }
}