package com.example.chat_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.chat_app.UIElement.LoadingDialog
import com.example.chat_app.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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
        // Firebase Auth to create a user
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                    task ->
                if (task.isSuccessful){
                    Toast.makeText(this@SignUp, "You have successfully registered!", Toast.LENGTH_LONG).show()
                    saveUserToFireDatabase()
                } else {
                    Toast.makeText(this@SignUp, task.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun saveUserToFireDatabase() {
        val uid = FirebaseAuth.getInstance().uid?:""// check if its null
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val username = findViewById<TextView>(R.id.edt_name).text.toString()
        val user = User(uid, username)
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
