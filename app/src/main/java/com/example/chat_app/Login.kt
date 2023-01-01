package com.example.chat_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.chat_app.UIElement.LoadingDialog
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    // firebase auth
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        val signUp = findViewById<TextView>(R.id.btnSignUp)
        signUp.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
        val forgotPassword = findViewById<TextView>(R.id.ForgotPassword)
        forgotPassword.setOnClickListener {
            val intent = Intent(this, Recovery::class.java)
            startActivity(intent)
        }
        val login = findViewById<Button>(R.id.btn_change_pass)
        login.setOnClickListener {
            login()
        }
    }
    // Check if user is logged in
    override fun onStart() {
        super.onStart()
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if(firebaseUser != null) {
            val intent = Intent(this@Login, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun login() {
        val email = findViewById<TextView>(R.id.changepassword_current).text.toString()
        val password = findViewById<TextView>(R.id.edit_password).text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill out email/password.", Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                else {
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
            .addOnFailureListener {
                Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}