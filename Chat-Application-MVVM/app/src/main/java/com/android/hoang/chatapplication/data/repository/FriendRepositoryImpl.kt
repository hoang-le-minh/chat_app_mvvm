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
    override suspend fun addFriend(userId: String): String = suspendCoroutine{ continuation ->
        val currentUser = FirebaseAuth.getInstance().currentUser
        val myRef = FirebaseDatabase.getInstance().getReference("friends").child(currentUser?.uid + userId)    // sender + receiver
        if (currentUser != null){
            val map = HashMap<String, String>()
            map["id_user_1"] = currentUser.uid
            map["id_user_2"] = userId
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

    override suspend fun acceptFriend(userId: String): String = suspendCoroutine{ continuation ->
        val currentUser = FirebaseAuth.getInstance().currentUser
        val myRef = FirebaseDatabase.getInstance().getReference("friends").child(userId + currentUser?.uid)
        if (currentUser != null){
            val map = HashMap<String, String>()
            map["relationship"] = FRIEND_RELATIONSHIP
            myRef.updateChildren(map as Map<String, Any>).addOnCompleteListener {
                if (it.isSuccessful){
                    continuation.resume(StringUtils.getString(R.string.ok))
                } else {
                    continuation.resume(StringUtils.getString(R.string.add_friend_failed))
                }
            }
        }
    }

    override suspend fun refuseFriend(user: UserFirebase): String = suspendCoroutine{ continuation ->
        val currentUser = FirebaseAuth.getInstance().currentUser
        val myRef = FirebaseDatabase.getInstance().getReference("friends").child(user.id + currentUser?.uid)
        if (currentUser != null){
            myRef.removeValue().addOnCompleteListener {
                if (it.isSuccessful){
                    continuation.resume(StringUtils.getString(R.string.ok))
                } else {
                    continuation.resume(StringUtils.getString(R.string.refuse_failed))
                }
            }
        }
    }

    override suspend fun cancelFriendRequest(userId: String): String = suspendCoroutine{ continuation ->
        val currentUser = FirebaseAuth.getInstance().currentUser
        val myRef = FirebaseDatabase.getInstance().getReference("friends").child(currentUser?.uid + userId)
        if (currentUser != null) {

            myRef.removeValue().addOnSuccessListener {
                continuation.resume(StringUtils.getString(R.string.ok))
            }.addOnFailureListener {
                continuation.resume(StringUtils.getString(R.string.cancel_failed))
            }
        }
    }

    override suspend fun unfriend(userId: String): String = suspendCoroutine{ continuation ->
        val currentUser = FirebaseAuth.getInstance().currentUser
        val myRef = FirebaseDatabase.getInstance().getReference("friends")
        if (currentUser != null){
            myRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapShot: DataSnapshot in snapshot.children) {
                        val friend = dataSnapShot.getValue(Friend::class.java) ?: continue
                        if ((friend.id_user_1 == currentUser.uid && friend.id_user_2 == userId) || (friend.id_user_2 == currentUser.uid && friend.id_user_1 == userId)){
                            dataSnapShot.ref.removeValue().addOnSuccessListener {
                                continuation.resume(StringUtils.getString(R.string.ok))
                            }.addOnFailureListener {
                                continuation.resume(StringUtils.getString(R.string.cancel_failed))
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(StringUtils.getString(R.string.cancel_failed))
                }

            })
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

    // 1: la ban be || 2: da gui loi moi || 3: cho xac nhan || 0: nothing
    override suspend fun checkRelationship(userId: String): Int = suspendCoroutine{ continuation ->
        val currentUser = FirebaseAuth.getInstance().currentUser
        val myRef = FirebaseDatabase.getInstance().getReference("friends")
        if (currentUser != null){
            var isResumed = false
            myRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var result = 0
                    for (dataSnapShot: DataSnapshot in snapshot.children){
                        val friend = dataSnapShot.getValue(Friend::class.java) ?: continue
                        if (friend.id_user_1 == currentUser.uid && friend.id_user_2 == userId){
                            result = if(friend.relationship == FRIEND_RELATIONSHIP){
                                1
                            } else {
                                2
                            }
                            break
                        }
                        if (friend.id_user_2 == currentUser.uid && friend.id_user_1 == userId){
                            result = if(friend.relationship == FRIEND_RELATIONSHIP){
                                1
                            } else {
                                3
                            }
                            break
                        }
                    }
                    if (!isResumed){
                        continuation.resume(result)
                        isResumed = true
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

}