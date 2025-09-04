package com.messenger.indiChat.models

data class GenericResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)
