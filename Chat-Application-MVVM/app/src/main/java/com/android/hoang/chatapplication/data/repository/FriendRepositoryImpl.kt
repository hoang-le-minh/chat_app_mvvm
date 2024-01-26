package com.android.hoang.chatapplication.data.repository

import android.util.Log
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.data.remote.model.Friend
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.domain.repository.FriendRepository
import com.android.hoang.chatapplication.util.Constants.ADD_FRIEND_RELATIONSHIP
import com.android.hoang.chatapplication.util.Constants.FRIEND_RELATIONSHIP
import com.android.hoang.chatapplication.util.Constants.LOG_TAG
import com.blankj.utilcode.util.StringUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FriendRepositoryImpl @Inject constructor(): FriendRepository {
    override suspend fun addFriend(user: UserFirebase): String = suspendCoroutine{ continuation ->
        val currentUser = FirebaseAuth.getInstance().currentUser
        val myRef = FirebaseDatabase.getInstance().getReference("friends").child(currentUser?.uid + user.id)
        if (currentUser != null){
            val map = HashMap<String, String>()
            map["id_user_1"] = currentUser.uid
            map["id_user_2"] = user.id
            map["relationship"] = ADD_FRIEND_RELATIONSHIP
            myRef.setValue(map).addOnCompleteListener {
                if (it.isSuccessful){
                    continuation.resume(StringUtils.getString(R.string.ok))
                } else {
                    continuation.resume(StringUtils.getString(R.string.add_friend_failed))
                }
            }
        }
    }

    override suspend fun getListSentFriend(): MutableList<String> = suspendCoroutine{ continuation ->
        val currentUser = FirebaseAuth.getInstance().currentUser
        val myRef = FirebaseDatabase.getInstance().getReference("friends")
        val listId = mutableListOf<String>()
        if (currentUser != null){
            var isResumed = false
            myRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    listId.clear()
                    for (dataSnapShot: DataSnapshot in snapshot.children){
                        val friend = dataSnapShot.getValue(Friend::class.java) ?: continue
                        if (friend.id_user_1 == currentUser.uid && friend.relationship == ADD_FRIEND_RELATIONSHIP){
                            listId.add(friend.id_user_2)
                        }
                    }

                    if (!isResumed){
                        continuation.resume(listId)
                        isResumed = true
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(LOG_TAG, "getListAddFriend.onCancelled: ${error.message}")
                }

            })
        }
    }

    override suspend fun getListRequestFriend(): MutableList<String> = suspendCoroutine{ continuation ->
        val currentUser = FirebaseAuth.getInstance().currentUser
        val myRef = FirebaseDatabase.getInstance().getReference("friends")
        val listId = mutableListOf<String>()
        if (currentUser != null){
            var isResumed = false
            myRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    listId.clear()
                    for (dataSnapShot: DataSnapshot in snapshot.children){
                        val friend = dataSnapShot.getValue(Friend::class.java) ?: continue
                        if (friend.id_user_2 == currentUser.uid && friend.relationship == ADD_FRIEND_RELATIONSHIP){
                            listId.add(friend.id_user_1)
                        }
                    }

                    if (!isResumed){
                        continuation.resume(listId)
                        isResumed = true
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(LOG_TAG, "getListAddFriend.onCancelled: ${error.message}")
                }

            })
        }
    }

    override suspend fun getListFriend(): MutableList<String> = suspendCoroutine { continuation ->
        val currentUser = FirebaseAuth.getInstance().currentUser
        val myRef = FirebaseDatabase.getInstance().getReference("friends")
        val listId = mutableListOf<String>()
        if (currentUser != null){
            var isResumed = false
            myRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    listId.clear()
                    for (dataSnapShot: DataSnapshot in snapshot.children){
                        val friend = dataSnapShot.getValue(Friend::class.java) ?: continue
                        if (friend.id_user_1 == currentUser.uid && friend.relationship == FRIEND_RELATIONSHIP){
                            listId.add(friend.id_user_2)
                        }
                        if (friend.id_user_2 == currentUser.uid && friend.relationship == FRIEND_RELATIONSHIP){
                            listId.add(friend.id_user_1)
                        }
                    }

                    if (!isResumed){
                        continuation.resume(listId)
                        isResumed = true
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(LOG_TAG, "getListAddFriend.onCancelled: ${error.message}")
                }

            })
        }
    }

    override suspend fun checkExistFriend(userId: String): String = suspendCoroutine{ continuation ->
        val currentUser = FirebaseAuth.getInstance().currentUser
        val addRef = FirebaseDatabase.getInstance().getReference("friends").child(currentUser?.uid + userId)
        val requestRef = FirebaseDatabase.getInstance().getReference("friends").child(userId + currentUser?.uid)
        if (currentUser != null){
            addRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val friend = snapshot.getValue(Friend::class.java)
                    if (friend == null || friend.id_user_1 == "" || friend.id_user_2 == "") {
                        requestRef.addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val friend1 = snapshot.getValue(Friend::class.java)
                                if (friend1 == null || friend1.id_user_1 == "" || friend1.id_user_2 == "") {
                                    continuation.resume(StringUtils.getString(R.string.not_exist))
                                    requestRef.removeEventListener(this)
                                }
                                else {
                                    continuation.resume(StringUtils.getString(R.string.exist))
                                    requestRef.removeEventListener(this)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }

                        })
                        addRef.removeEventListener(this)
                    }
                    else {
                        continuation.resume(StringUtils.getString(R.string.exist))
                        addRef.removeEventListener(this)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        }
    }


}