package com.example.connect.model

import com.google.firebase.Timestamp

class ChatMessageModel {
    data class ChatMessageModel(
        var message: String? = null,
        var senderId: String? = null,
        var timestamp: Timestamp? = null
    )
}