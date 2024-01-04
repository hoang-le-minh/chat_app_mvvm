package com.android.hoang.chatapplication.ui.main.home

data class UserTest(
    val name: String,
    val latestTime: String,
    val latestMessage: String,
    val imageResource: Int,
    val unreadLatest: Boolean,
    val unreadCount: Int
)