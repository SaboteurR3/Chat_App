package com.example.chat_app

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.example.chat_app.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class Profile : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()

        mDbRef.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if (mAuth.currentUser?.uid == currentUser?.uid) {
                        val saveUserUid = currentUser?.uid
                        val saveUserName = currentUser?.username
                        val saveUserPhoto = currentUser?.profileImageUrl
                        findViewById<TextView>(R.id.profile_uid).text = saveUserUid
                        findViewById<TextView>(R.id.profile_username).text = saveUserName
//                        findViewById<ImageView>(R.id.profile_picture).setImage setImageURI(saveUserPhoto)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
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
