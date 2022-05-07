package com.example.gallery.media.remote.album

data class Info(
    val commentThread: CommentThread? = null,
    val latestLikedUsers: Any? = null,
    val liked: Boolean? = null,
    val comments: Any? = null,
    val resourceType: Int? = null,
    val resourceId: Int? = null,
    val commentCount: Int? = null,
    val likedCount: Int? = null,
    val shareCount: Int? = null,
    val threadId: String? = null
)