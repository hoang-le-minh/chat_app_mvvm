package com.rikkei.training.chatapp.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import com.rikkei.training.chatapp.MainActivity
import com.rikkei.training.chatapp.databinding.ActivitySplashBinding
import com.rikkei.training.chatapp.ui.home.HomeActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed({
            nextActivity()
            finish()
        }, 3000)
    }

    private fun nextActivity(){
        val user = FirebaseAuth.getInstance().currentUser
        if(user == null){
            // go to login
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        } else {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}