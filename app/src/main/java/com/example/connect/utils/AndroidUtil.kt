package com.example.connect.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.connect.model.UserModel

class AndroidUtil {

    companion object {
        fun showToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        fun passUserModelAsIntent(intent: Intent, model: UserModel) {
            intent.putExtra("username", model.username)
            intent.putExtra("phone", model.phone)
            intent.putExtra("userId", model.userId)
            intent.putExtra("fcmToken", model.fcmToken)
        }

        fun getUserModelFromIntent(intent: Intent): UserModel {
            val userModel = UserModel()
            userModel.username = intent.getStringExtra("username")
            userModel.phone = intent.getStringExtra("phone")
            userModel.userId = intent.getStringExtra("userId")
            userModel.fcmToken = intent.getStringExtra("fcmToken")
            return userModel
        }

    }
}