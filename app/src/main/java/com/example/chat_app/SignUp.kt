package com.example.chat_app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.chat_app.UIElement.LoadingDialog
import com.example.chat_app.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class SignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val goToLogin = findViewById<TextView>(R.id.Login_2)
        goToLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
        val registerButton = findViewById<Button>(R.id.btnRegister)
        registerButton.setOnClickListener {
            registerUser()
        }
        val selectPhoto = findViewById<Button>(R.id.Select_Pohto_button)
        selectPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }
    var selectedPhoto: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // check what the selected image was
            selectedPhoto = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhoto)
            val selectPhoto = findViewById<Button>(R.id.Select_Pohto_button)

            val Select_Photo_Register = findViewById<CircleImageView>(R.id.Select_Photo_Register).setImageBitmap(bitmap)
            selectPhoto.alpha = 0f // hide 'upload image' button
        }
    }
    private fun registerUser() {
        // Get user input
        val username = findViewById<TextView>(R.id.edt_name).text.toString()
        val email = findViewById<TextView>(R.id.edt_email).text.toString()
        val password = findViewById<TextView>(R.id.edt_password).text.toString()
        // validations
        if(username == "") {
            Toast.makeText(this@SignUp, "Please enter username!", Toast.LENGTH_SHORT).show()
            return
        }
        else if(email == "") {
            Toast.makeText(this@SignUp, "Please enter email!", Toast.LENGTH_SHORT).show()
            return
        }
        else if(!(email.contains("@"))) {
            Toast.makeText(this@SignUp, "Incorrect email!", Toast.LENGTH_SHORT).show()
            return
        }
        else if(password == "") {
            Toast.makeText(this@SignUp, "Please enter password!", Toast.LENGTH_SHORT).show()
            return
        }
        else if(password.length < 8) {
            Toast.makeText(this@SignUp, "Password is too short!", Toast.LENGTH_SHORT).show()
            return
        }
        else if(selectedPhoto == null) {
            Toast.makeText(this@SignUp, "Your photo please!", Toast.LENGTH_SHORT).show()
            return
        }
        // Firebase Auth to create a user
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                    task ->
                if (task.isSuccessful){
                    Toast.makeText(this@SignUp, "You have successfully registered!", Toast.LENGTH_LONG).show()
                    uploadImageToFirebaseStorage()
                } else {
                    Toast.makeText(this@SignUp, task.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun uploadImageToFirebaseStorage() {
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhoto!!).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {  // Image URL
                it.toString()
            }
            saveUserToFireDatabase(it.toString())
        }
    }
    private fun saveUserToFireDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid?:""// check if its null
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val username = findViewById<TextView>(R.id.edt_name).text.toString()
        val user = User(uid, username, profileImageUrl)
        ref.setValue(user).addOnSuccessListener {
            // Save user to Firebase database
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            val loading = LoadingDialog(this)
            loading.startLoading()
            val handler = Handler()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    loading.isDismiss()
                    startActivity(intent)
                }
            }, 5000)
        }
    }
}
