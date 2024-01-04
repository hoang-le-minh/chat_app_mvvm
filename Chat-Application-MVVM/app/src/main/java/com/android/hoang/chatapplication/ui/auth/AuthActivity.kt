package com.android.hoang.chatapplication.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_auth
    }

    override fun prepareView(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
    }

}