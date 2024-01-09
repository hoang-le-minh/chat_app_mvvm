package com.android.hoang.chatapplication.ui.splash

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import androidx.core.content.ContextCompat
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseActivity
import com.android.hoang.chatapplication.ui.auth.AuthActivity
import com.android.hoang.chatapplication.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun prepareView(savedInstanceState: Bundle?) {
        supportActionBar?.hide()

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            val window: Window = window
//            window.statusBarColor = ContextCompat.getColor(this, R.color.color_1)
//        }

        Handler(Looper.getMainLooper()).postDelayed({
            nextActivity()
            finish()
        }, 2000)
    }

    private fun nextActivity(){
        val user = FirebaseAuth.getInstance().currentUser
        if(user == null){
            // go to login
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)

        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}