package com.example.gallery.media.remote.album

data class Info(
    val commentCount: Int,
    val commentThread: CommentThread,
    val comments: Any?,
    val latestLikedUsers: Any?,
    val liked: Boolean,
    val likedCount: Int,
    val resourceId: Int,
    val resourceType: Int,
    val shareCount: Int,
    val threadId: String
)