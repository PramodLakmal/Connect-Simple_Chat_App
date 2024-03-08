package com.example.connect

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.example.connect.model.ChatMessageModel
import com.example.connect.model.ChatroomModel
import com.example.connect.model.UserModel
import com.example.connect.utils.AndroidUtil
import com.example.connect.utils.FirebaseUtil
import com.example.easychat.adapter.ChatRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query

class ChatActivity : AppCompatActivity() {

    private lateinit var messageInput: EditText
    private lateinit var sendMessageBtn: ImageButton
    private lateinit var backBtn: ImageButton
    private  var otherUsername: TextView? = null
    private var recyclerView: RecyclerView? = null
    private lateinit var imageView: ImageView
    private var otherUser: UserModel? = null
    private lateinit var chatroomId: String
    private var chatroomModel: ChatroomModel? = null
    var adapter: ChatRecyclerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)

        // Get UserModel
        otherUser = AndroidUtil.getUserModelFromIntent(intent)
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId()!!, otherUser?.userId!!)
        messageInput = findViewById(R.id.chat_message_input)
        sendMessageBtn = findViewById(R.id.message_send_btn)
        backBtn = findViewById(R.id.back_btn)
        otherUsername = findViewById(R.id.other_username)
        recyclerView = findViewById(R.id.chat_recycler_view)
        imageView = findViewById(R.id.profile_pic_image_view)


        backBtn.setOnClickListener {
            val intent = Intent(this@ChatActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        otherUsername?.text = otherUser?.username

        sendMessageBtn.setOnClickListener {
            val message = messageInput.text.toString().trim()
            if (message.isEmpty()) {
                return@setOnClickListener
            }
            sendMessageToUser(message)
        }


        orCreateChatroomModel
        setupChatRecyclerView()


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun setupChatRecyclerView() {
        val query = FirebaseUtil.getChatroomMessageReference(chatroomId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<ChatMessageModel>()
            .setQuery(query, ChatMessageModel::class.java).build()
        adapter = ChatRecyclerAdapter(options, applicationContext)
        val manager = LinearLayoutManager(this)
        manager.setReverseLayout(true)
        recyclerView!!.setLayoutManager(manager)
        recyclerView!!.setAdapter(adapter)
        adapter!!.startListening()
        adapter!!.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                recyclerView!!.smoothScrollToPosition(0)
            }
        })
    }

    fun sendMessageToUser(message: String?) {
        chatroomModel?.lastMessageTimestamp = Timestamp.now()
        chatroomModel?.lastMessageSenderId = FirebaseUtil.currentUserId()
        chatroomModel?.lastMessage = message

        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel!!)
        val chatMessageModel =
            ChatMessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now())
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    messageInput!!.setText("")
                }
            }
    }

    private val orCreateChatroomModel: Unit
        get() {
            FirebaseUtil.getChatroomReference(chatroomId).get()
                .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                    if (task.isSuccessful) {
                        chatroomModel = task.result.toObject(ChatroomModel::class.java)
                        if (chatroomModel == null) {
                            //first time chat
                            chatroomModel = ChatroomModel(
                                chatroomId,
                                listOf(FirebaseUtil.currentUserId(), otherUser?.userId ?: ""),
                                Timestamp.now(),
                                ""
                            )
                            FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel!!)
                        }
                    }
                }
        }


}