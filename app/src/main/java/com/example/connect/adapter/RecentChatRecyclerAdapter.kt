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
import com.example.connect.model.ChatroomModel
import com.example.connect.model.UserModel
import com.example.connect.utils.AndroidUtil
import com.example.connect.utils.FirebaseUtil
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot

class RecentChatRecyclerAdapter(
    options: FirestoreRecyclerOptions<ChatroomModel?>,
    var context: Context?
) : FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder>(options) {
    override fun onBindViewHolder(
        holder: ChatroomModelViewHolder,
        position: Int,
        model: ChatroomModel
    ) {
        FirebaseUtil.getOtherUserFromChatroom(model.userIds)
            .get().addOnCompleteListener { task: Task<DocumentSnapshot> ->
                if (task.isSuccessful) {
                    val lastMessageSentByMe =
                        model.lastMessageSenderId == FirebaseUtil.currentUserId()
                    val otherUserModel = task.result.toObject(UserModel::class.java)

                    if (otherUserModel != null) {
                        holder.usernameText.text = otherUserModel.username
                    }
                    if (lastMessageSentByMe) holder.lastMessageText.text =
                        "You : " + model.lastMessage else holder.lastMessageText.text =
                        model.lastMessage
                    holder.lastMessageTime.text =
                        FirebaseUtil.timestampToString(model.lastMessageTimestamp)
                    holder.itemView.setOnClickListener {
                        //navigate to chat activity
                        val intent = Intent(context, ChatActivity::class.java)
                        if (otherUserModel != null) {
                            AndroidUtil.passUserModelAsIntent(intent, otherUserModel)
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context!!.startActivity(intent)
                    }
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatroomModelViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false)
        return ChatroomModelViewHolder(view)
    }

    inner class ChatroomModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var usernameText: TextView
        var lastMessageText: TextView
        var lastMessageTime: TextView
        var profilePic: ImageView

        init {
            usernameText = itemView.findViewById(R.id.user_name_text)
            lastMessageText = itemView.findViewById(R.id.last_message_text)
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text)
            profilePic = itemView.findViewById(R.id.profile_pic_image_view)
        }
    }
}