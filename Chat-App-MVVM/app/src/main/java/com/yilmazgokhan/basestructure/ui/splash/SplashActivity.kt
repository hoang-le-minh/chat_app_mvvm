package com.yilmazgokhan.basestructure.ui.splash

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import androidx.core.content.ContextCompat
import com.yilmazgokhan.basestructure.R
import com.yilmazgokhan.basestructure.base.BaseActivity
import com.yilmazgokhan.basestructure.databinding.ActivitySplashBinding
import com.yilmazgokhan.basestructure.ui.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun prepareView(savedInstanceState: Bundle?) {
        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.statusBarColor = ContextCompat.getColor(this, R.color.color_1)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }


}