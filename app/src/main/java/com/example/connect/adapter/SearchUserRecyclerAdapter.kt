package com.example.connect.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.connect.ChatActivity
import com.example.connect.R
import com.example.connect.model.UserModel
import com.example.connect.utils.AndroidUtil
import com.example.connect.utils.FirebaseUtil
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Task


class SearchUserRecyclerAdapter(
    options: FirestoreRecyclerOptions<UserModel?>,
    var context: Context
) : FirestoreRecyclerAdapter<UserModel, SearchUserRecyclerAdapter.UserModelViewHolder>(options) {
    override fun onBindViewHolder(holder: UserModelViewHolder, position: Int, model: UserModel) {
        holder.usernameText.text = model.username
        holder.phoneText.text = model.phone
        if (model.userId == FirebaseUtil.currentUserId()) {
            holder.usernameText.text = model.username + " (Me)"
        }

        FirebaseUtil.getOtherProfilePicStorageRef(model?.userId).downloadUrl
            .addOnCompleteListener { task: Task<Uri?> ->
                if (task.isSuccessful) {
                    val uri: Uri? = task.result
                    AndroidUtil.setProfilePic(context, uri, holder.profilePic)
                }
            }

        holder.itemView.setOnClickListener {
            //navigate to chat activity
            val intent = Intent(context, ChatActivity::class.java)
            AndroidUtil.passUserModelAsIntent(intent, model)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserModelViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row, parent, false)
        return UserModelViewHolder(view)
    }

    inner class UserModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var usernameText: TextView
        var phoneText: TextView
        var profilePic: ImageView

        init {
            usernameText = itemView.findViewById(R.id.user_name_text)
            phoneText = itemView.findViewById(R.id.phone_text)
            profilePic = itemView.findViewById(R.id.profile_pic_image_view)
        }
    }
}
