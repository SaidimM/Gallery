package com.example.gallery.base.utils

import android.animation.Animator
import android.view.ViewPropertyAnimator
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener

object AnimationUtils {
    fun Animation.setListeners(
        onStart: (() -> Unit)? = null,
        onEnd: (() -> Unit)? = null,
        onRepeat: (() -> Unit)? = null
    ) {
        this.setAnimationListener(object : AnimationListener {
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

    fun ViewPropertyAnimator.setListeners(
        onStart: (() -> Unit)? = null,
        onEnd: (() -> Unit)? = null,
        onRepeat: (() -> Unit)? = null,
        onCancel: (() -> Unit)? = null
    ): ViewPropertyAnimator {
        this.setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                if (onStart != null) onStart()
            }

            override fun onAnimationEnd(animation: Animator?) {
                if (onEnd != null) onEnd()
            }

            override fun onAnimationCancel(animation: Animator?) {
                if (onCancel != null) onCancel()
            }

            override fun onAnimationRepeat(animation: Animator?) {
                if (onRepeat != null) onRepeat()
            }
        })
        return this
    }
}