package com.rikkei.training.chatapp.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.rikkei.training.chatapp.MainActivity
import com.rikkei.training.chatapp.R
import com.rikkei.training.chatapp.TAG
import com.rikkei.training.chatapp.databinding.ActivityHomeBinding
import com.rikkei.training.chatapp.ui.profile.ProfileActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showUserInformation()

        binding.btnLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.btnPushData.setOnClickListener {
            onClickPushData()
        }
//        binding.btnGetData.setOnClickListener {
//            readDatabase()
//        }
    }

    private fun onClickPushData() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")

        val message = binding.edtData.text.toString().trim()
        myRef.setValue(message)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue<String>()
                Log.d(TAG, "Value is: $value")
                binding.txtData.text = value
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    private fun readDatabase(){
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue<String>()
                Log.d(TAG, "Value is: $value")
                binding.txtData.text = value
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    private fun showUserInformation(){
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        } else {
            val name = user.displayName
            if (name == null){
                binding.txtUsername.visibility = View.INVISIBLE
            } else {
                binding.txtUsername.visibility = View.VISIBLE
                binding.txtUsername.text = name
            }
            binding.txtEmail.text = user.email

            val photoUrl = user.photoUrl
            Glide.with(this).load(photoUrl).error(R.drawable.no_image).into(binding.userAvt)
        }
    }
}