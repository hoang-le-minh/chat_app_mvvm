package com.android.hoang.chatapplication.data.repository

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.data.remote.model.UserResponse
import com.android.hoang.chatapplication.di.qualifier.IoDispatcher
import com.android.hoang.chatapplication.domain.repository.UserRepository
import com.android.hoang.chatapplication.util.Constants.LOG_TAG
import com.blankj.utilcode.util.StringUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.isActive
import javax.inject.Inject
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Implementation of [UserRepository] class
 */
class UserRepositoryImpl @Inject constructor(private val userDataSource: UserDataSource) :
    UserRepository {

    override suspend fun getCurrentUser(): UserFirebase? = suspendCoroutine { continuation ->
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null){
            var isResumed = false
            val myRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.uid)
            myRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(UserFirebase::class.java)
                    Log.d(LOG_TAG, "getCurrentUser.onDataChange: ${user.toString()}")
                    if (!isResumed){
                        continuation.resume(user)
                        isResumed = true
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(LOG_TAG, "onCancelled: ${error.message}")
                    if (!isResumed){
                        continuation.resume(null)
                        isResumed = true
                    }
                }

            })
        } else continuation.resume(null)


    }
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

    override suspend fun getAllUser(): List<UserFirebase> = suspendCoroutine { continuation ->
        val userList = mutableListOf<UserFirebase>()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val myRef = FirebaseDatabase.getInstance().getReference("users")
        if(currentUser != null){
            var isResumed = false
            myRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()
                    for (dataSnapshot: DataSnapshot in snapshot.children){
                        val user = dataSnapshot.getValue(UserFirebase::class.java) ?: return
                        if(user.id != currentUser.uid){
                            userList.add(user)
                            Log.d(LOG_TAG, "onDataChange: ${user.id}")
                        }
                    }
                    if (!isResumed){
                        continuation.resume(userList)
                        isResumed = true
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(LOG_TAG, "onCancelled: ${error.message}")
                }

            })
        }

    }
}