package com.example.connect.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


object FirebaseUtil {
    fun currentUserId(): String? {
        return FirebaseAuth.getInstance().uid
    }

    fun isLoggedIn(): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        return user != null
    }

    fun currentUserDetails(): DocumentReference {
        return FirebaseFirestore.getInstance().collection("users").document((currentUserId())!!)
    }

    fun allUserCollectionReference(): CollectionReference {
        return FirebaseFirestore.getInstance().collection("users")
    }

    fun getChatroomReference(chatroomId: String?): DocumentReference {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId!!)
    }

    fun getChatroomId(userId1: String, userId2: String): String {
        return if (userId1.hashCode() < userId2.hashCode()) {
            userId1 + "_" + userId2
        } else {
            userId2 + "_" + userId1
        }
    }

}
