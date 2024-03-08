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
import com.example.connect.utils.AndroidUtil
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import java.util.concurrent.TimeUnit

class LoginOtpActivity : AppCompatActivity() {

    private lateinit var phoneNumber: String
    private var timeoutSeconds = 60L
    lateinit var verificationCode: String
    private lateinit var otpInput: EditText
    private lateinit var nextBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var resendOtpTextView: TextView
    private var mAuth = FirebaseAuth.getInstance()

    private lateinit var resendingToken: ForceResendingToken
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_otp)

        otpInput = findViewById(R.id.login_otp)
        nextBtn = findViewById(R.id.login_otp_next_btn)
        progressBar = findViewById(R.id.login_progress_bar)
        resendOtpTextView = findViewById(R.id.resend_otp_textview)

        phoneNumber = intent.extras!!.getString("phone")!!

        sendOtp(phoneNumber, false)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun sendOtp(phoneNumber: String?, isResend: Boolean) {
        setInProgress(true)
        val builder = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phoneNumber!!)
            .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    signIn(phoneAuthCredential)
                    setInProgress(false)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    AndroidUtil.showToast(applicationContext, "OTP verification failed")
                    setInProgress(false)
                }

                override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                    super.onCodeSent(s, forceResendingToken)
                    verificationCode = s
                    resendingToken = forceResendingToken
                    AndroidUtil.showToast(applicationContext, "OTP sent successfully")
                    setInProgress(false)
                }
            })
        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(
                builder.setForceResendingToken(resendingToken).build()
            )
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build())
        }
    }

    private fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            progressBar.visibility = View.VISIBLE
            nextBtn.visibility = View.GONE
        } else {
            progressBar.visibility = View.GONE
            nextBtn.visibility = View.VISIBLE
        }
    }

    fun signIn(phoneAuthCredential: PhoneAuthCredential?) {
        //login and go to next activity
        setInProgress(true)
        mAuth.signInWithCredential(phoneAuthCredential!!).addOnCompleteListener { task ->
            setInProgress(false)
            if (task.isSuccessful) {
                val intent = Intent(
                    this@LoginOtpActivity,
                    LoginUsernameActivity::class.java
                )
                intent.putExtra("phone", phoneNumber)
                startActivity(intent)
            } else {
                AndroidUtil.showToast(applicationContext, "OTP verification failed")
            }
        }
    }

}