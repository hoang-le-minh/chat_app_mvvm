package com.yilmazgokhan.basestructure.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yilmazgokhan.basestructure.base.BaseActivity
import com.yilmazgokhan.basestructure.R
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