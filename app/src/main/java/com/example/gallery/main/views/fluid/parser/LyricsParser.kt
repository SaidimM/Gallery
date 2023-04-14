package com.example.gallery.main.views.fluid.parser

import com.example.gallery.main.views.fluid.Lyrics

interface LyricsParser {
    fun parse(input: String): Lyrics?
}
