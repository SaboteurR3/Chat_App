package com.example.chat_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Recovery : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recovery)
        val signUp_2 = findViewById<TextView>(R.id.SignUp_2)
        signUp_2.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
        val resetButton = findViewById<Button>(R.id.resetButton)
        resetButton.setOnClickListener {
            // Get user input
            val userEmail = findViewById<EditText>(R.id.Register_usrn).text.toString().trim{it <= ' '} // trim empty spaces
            if(userEmail.isEmpty()){
                Toast.makeText(this@Recovery, "Please enter email!", Toast.LENGTH_SHORT).show()
            } else {
                FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail)
                    .addOnCompleteListener {
                            task -> if(task.isSuccessful) {
                        Toast.makeText(this@Recovery, "Email sent successfully to reset your password", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@Recovery, task.exception!!.message.toString() , Toast.LENGTH_SHORT).show()
                    }
                    }
            }
        }
    }
}