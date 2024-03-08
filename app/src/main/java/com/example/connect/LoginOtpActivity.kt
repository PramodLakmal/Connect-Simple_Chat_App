package com.example.connect

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class LoginOtpActivity : AppCompatActivity() {

    lateinit var phoneNumber: String
    var timeoutSeconds = 60L
    lateinit var verificationCode: String
    lateinit var otpInput: EditText
    lateinit var nextBtn: Button
    lateinit var progressBar: ProgressBar
    lateinit var resendOtpTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_otp)

        progressBar = findViewById(R.id.login_progress_bar)
        resendOtpTextView = findViewById(R.id.resend_otp_textview)

        phoneNumber = intent.extras!!.getString("phone")!!
        Toast.makeText(this, phoneNumber, Toast.LENGTH_SHORT).show()




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}