package com.example.gallery.media.remote.artist

data class ArtistResult(
    val artist: Artist,
    val code: Int,
    val hotSongs: List<HotSong>,
    val more: Boolean
)