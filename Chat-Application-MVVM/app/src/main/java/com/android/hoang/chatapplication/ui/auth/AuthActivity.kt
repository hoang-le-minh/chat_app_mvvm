package com.android.hoang.chatapplication.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseActivity
import com.android.hoang.chatapplication.databinding.ActivityAuthBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : BaseActivity<ActivityAuthBinding>() {

    override fun prepareView(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
    }

    override fun getActivityBinding(inflater: LayoutInflater) = ActivityAuthBinding.inflate(layoutInflater)

}