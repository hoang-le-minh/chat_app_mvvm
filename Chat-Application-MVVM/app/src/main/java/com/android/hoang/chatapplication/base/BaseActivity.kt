package com.android.hoang.chatapplication.base

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.util.Loading
import com.blankj.utilcode.util.LogUtils
import java.util.Locale

/**
 * Base class for activity instances
 */
abstract class BaseActivity<B: ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: B

    //region vars
    private lateinit var mLoading: Loading
    //endregion

    abstract fun getActivityBinding(inflater: LayoutInflater): B

    /**
     * Prepare UI Components
     */
    abstract fun prepareView(savedInstanceState: Bundle?)

    /**
     * Override onCreate method
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtils.d("$this onCreate: ")

        binding = getActivityBinding(layoutInflater)

        // window transparent status bar
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        //Set language
        val sharedPref = getSharedPreferences("current_language", Context.MODE_PRIVATE)
        val currentLang = sharedPref.getString("current_language", "")

        val config = resources.configuration
        val locale = Locale(currentLang)
        Locale.setDefault(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            config.setLocale(locale)
        } else {
            config.locale = locale
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            createConfigurationContext(config)
        }
        resources.updateConfiguration(config, resources.displayMetrics)

        //Set layout
        setContentView(binding.root)

        //Set custom loading dialog
        mLoading = Loading(this, R.style.StyleLoading)

        //Set view
        prepareView(savedInstanceState)

        supportActionBar?.hide()
    }

    //region Custom Loading Dialog's methods
    /**
     * Show loading
     */
    fun showLoading() {
        try {
            if (!mLoading.isShowing && !isFinishing)
                mLoading.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Hide loading
     */
    fun hideLoading() {
        try {
            if (mLoading.isShowing)
                mLoading.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    //endregion

    override fun onStart() {
        super.onStart()
        LogUtils.d("$this onStart")
    }

    override fun onResume() {
        super.onResume()
        LogUtils.d("$this onResume")
    }

    override fun onPause() {
        super.onPause()
        LogUtils.d("$theme onPause")
    }

    override fun onStop() {
        super.onStop()
        LogUtils.d("$this onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.d("$this onDestroy")
    }
    //endregion
}