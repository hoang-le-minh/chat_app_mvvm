package com.android.hoang.chatapplication.data.remote.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserFirebase(var id: String = "", var email: String = "", var username: String = "", var imageUrl: String = "", var phoneNumber: String = "", var dateOfBirth: String = ""): Parcelable {

}

@Parcelize
data class ListUser(var list: List<UserFirebase>?): Parcelable{

}

@Parcelize
data class ListString(var list: List<String>): Parcelable{

}