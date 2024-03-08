package com.example.connect

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.connect.model.UserModel
import com.example.connect.utils.AndroidUtil
import com.example.connect.utils.FirebaseUtil
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.UploadTask


class ProfileFragment : Fragment() {

    var profilePic: ImageView? = null
    var usernameInput: EditText? = null
    var phoneInput: EditText? = null
    var updateProfileBtn: Button? = null
    var progressBar: ProgressBar? = null
    var logoutBtn: TextView? = null
    var currentUserModel: UserModel? = null
    var imagePickLauncher: ActivityResultLauncher<Intent>? = null
    var selectedImageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePickLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data?.data != null) {
                    selectedImageUri = data.data
                    AndroidUtil.setProfilePic(context, selectedImageUri, profilePic)
                }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        profilePic = view.findViewById(R.id.profile_image_view)
        usernameInput = view.findViewById(R.id.profile_username)
        phoneInput = view.findViewById(R.id.profile_phone)
        updateProfileBtn = view.findViewById(R.id.profle_update_btn)
        progressBar = view.findViewById(R.id.profile_progress_bar)
        logoutBtn = view.findViewById(R.id.logout_btn)

        userData

        updateProfileBtn?.setOnClickListener { updateBtnClick() }

        logoutBtn?.setOnClickListener {
            FirebaseUtil.logout()
            val intent = Intent(context, SplashActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        profilePic?.setOnClickListener(View.OnClickListener { v: View? ->
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
                .createIntent { intent: Intent ->
                    imagePickLauncher!!.launch(intent)
                    null
                }
        })

        return view
    }

    private fun updateBtnClick() {
        val newUsername = usernameInput!!.text.toString()
        if (newUsername.isEmpty() || newUsername.length < 3) {
            usernameInput!!.error = "Username length should be at least 3 chars"
            return
        }
        currentUserModel?.username = newUsername
        setInProgress(true)

        if (selectedImageUri != null) {
            FirebaseUtil.currentProfilePicStorageRef.putFile(selectedImageUri!!)
                .addOnCompleteListener { task: Task<UploadTask.TaskSnapshot?>? -> updateToFirestore() }
        } else {
            updateToFirestore()
        }
    }
    private fun updateToFirestore() {
        FirebaseUtil.currentUserDetails().set(currentUserModel!!)
            .addOnCompleteListener { task: Task<Void?> ->
                setInProgress(false)
                if (task.isSuccessful) {
                    context?.let { AndroidUtil.showToast(it, "Updated successfully") }
                } else {
                    context?.let { AndroidUtil.showToast(it, "Updated failed") }
                }
            }
    }

    private val userData: Unit
        get() {
            setInProgress(true)

            FirebaseUtil.currentProfilePicStorageRef.downloadUrl
                .addOnCompleteListener { task: Task<Uri?> ->
                    if (task.isSuccessful) {
                        val uri: Uri? = task.result
                        AndroidUtil.setProfilePic(context, uri, profilePic)
                    }
                }

            FirebaseUtil.currentUserDetails().get()
                .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                    setInProgress(false)
                    currentUserModel = task.getResult().toObject(UserModel::class.java)
                    usernameInput?.setText(currentUserModel?.username ?: "")
                    phoneInput?.setText(currentUserModel?.phone ?: "")
                }
        }

    private fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            progressBar!!.visibility = View.VISIBLE
            updateProfileBtn!!.visibility = View.GONE
        } else {
            progressBar!!.visibility = View.GONE
            updateProfileBtn!!.visibility = View.VISIBLE
        }
    }

}