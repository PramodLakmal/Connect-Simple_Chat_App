package com.example.connect

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.connect.adapter.RecentChatRecyclerAdapter
import com.example.connect.model.ChatroomModel
import com.example.connect.utils.FirebaseUtil
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query


class ChatFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    var adapter: RecentChatRecyclerAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        recyclerView = view.findViewById(R.id.recyler_view)
        setupRecyclerView()
        return view
    }

    fun setupRecyclerView() {
        val query = FirebaseUtil.allChatroomCollectionReference()
            .whereArrayContains("userIds", FirebaseUtil.currentUserId()!!)
            .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<ChatroomModel>()
            .setQuery(query, ChatroomModel::class.java).build()
        adapter = RecentChatRecyclerAdapter(options, context)
        recyclerView!!.setLayoutManager(LinearLayoutManager(context))
        recyclerView!!.setAdapter(adapter)
        adapter!!.startListening()
    }

    override fun onStart() {
        super.onStart()
        if (adapter != null) adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (adapter != null) adapter!!.stopListening()
    }

    override fun onResume() {
        super.onResume()
        if (adapter != null) adapter!!.notifyDataSetChanged()
    }
}