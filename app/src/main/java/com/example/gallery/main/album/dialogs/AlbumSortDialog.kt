package com.example.gallery.main.album.dialogs

import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialog

class AlbumSortDialog(private val context: Context): BottomSheetDialog(context) {
    fun showDialog() {
        setCancelable(true)
    }
}