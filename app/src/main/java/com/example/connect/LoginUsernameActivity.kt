package com.example.connect

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.connect.model.UserModel
import com.example.connect.utils.FirebaseUtil
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.toObject

class LoginUsernameActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var letMeInBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var phoneNumber: String
    private var userModel : UserModel? = null

    private lateinit var usernameHint: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_username)

        usernameInput = findViewById(R.id.login_username)
        letMeInBtn = findViewById(R.id.login_let_me_in_btn)
        progressBar = findViewById(R.id.login_progress_bar)

        usernameHint = findViewById(R.id.username_hint)

        phoneNumber = intent.extras!!.getString("phone")!!
        getUsername()

        letMeInBtn.setOnClickListener {
            setUsername()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setUsername() {
        val username = usernameInput.text.toString()
        if (username.isEmpty() || username.length < 3) {
            usernameInput.error = "Username length should be at least 3 chars"
            return
        }
        setInProgress(true)
        if (userModel != null) {
            userModel?.username = username
        } else {
            userModel = FirebaseUtil.currentUserId()
                ?.let { UserModel(phoneNumber, username, Timestamp.now(), it) }
        }

        userModel?.let { userModel ->
            FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener { task ->
                setInProgress(false)
                if (task.isSuccessful) {
                    val intent = Intent(this@LoginUsernameActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
        }
    }

    private fun getUsername() {
        setInProgress(true)
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener { task ->
            setInProgress(false)
            if (task.isSuccessful) {
                val documentSnapshot = task.result
                userModel = documentSnapshot.toObject<UserModel>()
                if (userModel != null) {
                    // Display the username if available
                    usernameInput.setText(userModel!!.username)
                    usernameHint.text = getString(R.string.welcome_back)

                    // Make the username input field uneditable
                    usernameInput.isEnabled = false
                }
            }
        }
    }

    private fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            progressBar.visibility = View.VISIBLE
            letMeInBtn.visibility = View.GONE
        } else {
            progressBar.visibility = View.GONE
            letMeInBtn.visibility = View.VISIBLE
        }
    }
}
