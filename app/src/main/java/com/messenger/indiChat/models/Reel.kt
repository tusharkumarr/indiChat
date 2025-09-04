package com.messenger.indiChat.models

data class Reel(
    val id: String,
    val userId: String,
    val caption: String,
    val thumbnailGif: String,
    val videoUrl: String,
    val likes: List<String>, // list of userIds who liked
    val createdAt: String
)
