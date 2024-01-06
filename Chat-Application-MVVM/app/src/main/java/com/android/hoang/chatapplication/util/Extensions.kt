package com.android.hoang.chatapplication.util

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.android.hoang.chatapplication.R
import com.blankj.utilcode.util.StringUtils
import java.util.regex.Matcher
import java.util.regex.Pattern

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
        negativeButton(R.string.cancel){
            dismiss()
        }
    }
}

// validate email
private val VALID_EMAIL_ADDRESS_REGEX =
    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
fun emailValidator(emailStr: String): Boolean {
    val matcher: Matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr)
    return matcher.matches()
}