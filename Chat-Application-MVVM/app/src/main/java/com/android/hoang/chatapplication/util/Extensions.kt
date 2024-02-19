package com.android.hoang.chatapplication.util

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.android.hoang.chatapplication.R
import com.blankj.utilcode.util.StringUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
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

fun dateOfBirthValidator(dob: String): Boolean {
    if (dob == "") return true
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    dateFormat.isLenient = false

    try {
        val parsedDate = dateFormat.parse(dob)
        val currentDate = Calendar.getInstance().time

        if (parsedDate != null && parsedDate.before(currentDate)) {

            return true
        }
    } catch (e: ParseException) {
        // The date could not be parsed, or it's after the current date, so it's not valid
        return false
    }

    return false
}

fun listStickerResource(): List<Int> {
    return listOf(
        R.drawable.sticker_1,
        R.drawable.sticker_2,
        R.drawable.sticker_3,
        R.drawable.sticker_4,
        R.drawable.sticker_5,
        R.drawable.sticker_6,
        R.drawable.sticker_7,
        R.drawable.sticker_8,
        R.drawable.sticker_9,
        R.drawable.sticker_10,
        R.drawable.sticker_11,
        R.drawable.sticker_12,
    )
}