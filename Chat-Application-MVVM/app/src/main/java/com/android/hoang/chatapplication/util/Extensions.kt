package com.android.hoang.chatapplication.util

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.android.hoang.chatapplication.R
import com.blankj.utilcode.util.StringUtils

/**
 * Show alert dialog
 */
fun Context.showMessage(
    stringId: Int,
    onPositive: ((MaterialDialog) -> Unit)? = null
) {
    MaterialDialog(this).show {
        cancelable(false)
        cancelOnTouchOutside(false)
        message(text = StringUtils.getString(stringId))
        positiveButton(R.string.ok) {
            onPositive?.invoke(it)
        }
    }
}