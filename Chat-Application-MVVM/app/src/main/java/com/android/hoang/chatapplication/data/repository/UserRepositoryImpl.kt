package com.android.hoang.chatapplication.data.repository

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.domain.repository.UserRepository
import com.android.hoang.chatapplication.util.Constants.LOG_TAG
import com.blankj.utilcode.util.StringUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Implementation of [UserRepository] class
 */
class UserRepositoryImpl @Inject constructor(private val userDataSource: UserDataSource) :
    UserRepository {

    override suspend fun getUser(username: String) = userDataSource.getUser(username = username)
    override suspend fun signUp(email: String, password: String): String = suspendCoroutine{ continuation ->
        // call firebase
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    continuation.resume(StringUtils.getString(R.string.ok))
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(LOG_TAG, "createUserWithEmail:failure", task.exception)
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        // Địa chỉ email đã được sử dụng.
                        Log.d(LOG_TAG, "Địa chỉ email đã được sử dụng.")
                        // Thực hiện các thao tác xử lý khi có lỗi trùng địa chỉ email
                        continuation.resume(StringUtils.getString(R.string.email_exist))
                    } else {
                        // Xử lý các lỗi khác
                        Log.e("TAG", "Lỗi đăng ký: ${task.exception?.message}")
                        continuation.resume(StringUtils.getString(R.string.something_went_wrong))
                    }

                }
            }
    }

    override suspend fun signIn(email: String, password: String): String = suspendCoroutine { continuation ->
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(LOG_TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    continuation.resume(StringUtils.getString(R.string.ok))
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(LOG_TAG, "signInWithEmail:failure", task.exception)
                    continuation.resume(StringUtils.getString(R.string.wrong_email_or_password))

                }
            }
    }
}