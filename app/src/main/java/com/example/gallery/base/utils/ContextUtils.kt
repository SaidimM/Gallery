package com.example.gallery.base.utils

import android.content.Context
import android.content.res.Configuration

object ContextUtils {

    fun Context.isLightTheme(): Boolean {
        val flag = this.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        return flag != Configuration.UI_MODE_NIGHT_YES
    }
}