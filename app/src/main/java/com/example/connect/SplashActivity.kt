package com.example.connect

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.connect.model.UserModel
import com.example.connect.utils.AndroidUtil
import com.example.connect.utils.FirebaseUtil
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (intent.extras != null) {
            //from notification
            val userId = intent.extras!!.getString("userId")
            FirebaseUtil.allUserCollectionReference().document(userId!!).get()
                .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                    if (task.isSuccessful) {
                        val model = task.result.toObject(UserModel::class.java)
                        val mainIntent = Intent(this, MainActivity::class.java)
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        startActivity(mainIntent)
                        val intent = Intent(this, ChatActivity::class.java)
                        if (model != null) {
                            AndroidUtil.passUserModelAsIntent(intent, model)
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                }
        } else {

            Handler(Looper.getMainLooper()).postDelayed({

                if (FirebaseUtil.isLoggedIn()) {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                } else {
                    startActivity(Intent(this@SplashActivity, LoginPhoneNumberActivity::class.java))
                }
                finish()

            }, 1000)
        }

    }
}