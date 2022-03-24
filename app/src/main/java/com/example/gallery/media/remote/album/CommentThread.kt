package com.example.gallery.media.remote.album

data class CommentThread(
    val commentCount: Int,
    val hotCount: Int,
    val id: String,
    val latestLikedUsers: Any?,
    val likedCount: Int,
    val resourceId: Int,
    val resourceInfo: ResourceInfo,
    val resourceOwnerId: Int,
    val resourceTitle: String,
    val resourceType: Int,
    val shareCount: Int
)