package com.example.chat_app

import android.app.Dialog
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.chat_app.UIElement.LoadingDialog
import com.example.chat_app.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.lang.System.load

class Profile : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()

        val changePasswordBtn = findViewById<Button>(R.id.btn_change_pass)
        changePasswordBtn.setOnClickListener {
            changePassword()
        }

        mDbRef.child("users").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if (mAuth.currentUser?.uid == currentUser?.uid) {
                        val saveUserUid = currentUser?.uid
                        val saveUserName = currentUser?.username
                        findViewById<TextView>(R.id.profile_uid).text = saveUserUid
                        findViewById<TextView>(R.id.profile_username).text = saveUserName
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    private fun changePassword() {
        val currentPassword = findViewById<EditText>(R.id.changepassword_current).text.toString()
        val newPassword = findViewById<EditText>(R.id.changepassword_new).text.toString()
        val repeatPassword = findViewById<EditText>(R.id.changepassword_new2).text.toString()

        if(currentPassword.isNotEmpty() && newPassword.isNotEmpty() && repeatPassword.isNotEmpty()){
            if(newPassword.equals(repeatPassword)) {
                val user = mAuth.currentUser
                val credential = EmailAuthProvider.getCredential(user?.email!!, currentPassword.toString())
                user?.reauthenticate(credential)?.addOnCompleteListener{
                    if(it.isSuccessful) {
                        user?.updatePassword(newPassword.toString())?.addOnCompleteListener {
                            task ->
                            if(task.isSuccessful) {
                                Toast.makeText(this@Profile, "Password changed successfully", Toast.LENGTH_SHORT).show()
                                mAuth.signOut()
                                val intent = Intent(this, Login::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                val loading = LoadingDialog(this)
                                loading.startLoading()
                                val handler = Handler()
                                handler.postDelayed(object : Runnable {
                                    override fun run() {
                                        loading.isDismiss()
                                        startActivity(intent)
                                    }
                                }, 2000)
                            }
                        }
                    } else {
                        Toast.makeText(this@Profile, "Something went wrong!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this@Profile, "Inputs don't match!", Toast.LENGTH_SHORT).show()
                return
            }
        } else {
            Toast.makeText(this@Profile, "Please enter all the fields!", Toast.LENGTH_SHORT).show()
            return
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu2, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.messages) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.profile) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            return true
        }
        return true
    }
}
