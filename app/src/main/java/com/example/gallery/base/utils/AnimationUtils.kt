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
    ): Animation {
        this.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                if (onStart != null) {
                    onStart()
                }
            }

            override fun onAnimationEnd(animation: Animation) {
                if (onEnd != null) {
                    onEnd()
                }
            }

            override fun onAnimationRepeat(animation: Animation) {
                if (onRepeat != null) {
                    onRepeat()
                }
            }
        })
        return this
    }

    fun Animation.onAnimationStart(onStart: (() -> Unit)): Animation {
        this.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) = onStart()
            override fun onAnimationEnd(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
        })
        return this
    }

    fun Animation.onAnimationEnd(onEnd: (() -> Unit)): Animation {
        this.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) = onEnd()
            override fun onAnimationRepeat(animation: Animation) {}
        })
        return this
    }

    fun ViewPropertyAnimator.onAnimationStart(onStart: (() -> Unit)): ViewPropertyAnimator {
        this.setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) = onStart()
            override fun onAnimationEnd(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        return this
    }

    fun ViewPropertyAnimator.onAnimationEnd(onEnd: (() -> Unit)): ViewPropertyAnimator {
        this.setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) = onEnd()
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        return this
    }

    fun ViewPropertyAnimator.setListeners(
        onStart: (() -> Unit)? = null,
        onEnd: (() -> Unit)? = null,
        onRepeat: (() -> Unit)? = null,
        onCancel: (() -> Unit)? = null
    ): ViewPropertyAnimator {
        this.setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                if (onStart != null) onStart()
            }

            override fun onAnimationEnd(animation: Animator) {
                if (onEnd != null) onEnd()
            }

            override fun onAnimationCancel(animation: Animator) {
                if (onCancel != null) onCancel()
            }

            override fun onAnimationRepeat(animation: Animator) {
                if (onRepeat != null) onRepeat()
            }
        })
        return this
    }
}