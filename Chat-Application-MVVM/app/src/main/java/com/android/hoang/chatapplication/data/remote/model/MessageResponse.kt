package com.android.hoang.chatapplication.data.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MessageResponse(
    @SerializedName("id")
    @Expose
    val id: String? = null,
    @SerializedName("object")
    @Expose
    val strObject: String? = null,
    @SerializedName("choices")
    @Expose
    val choices: List<Choice>? = null,

) {
}

data class MessageParamPost(
    var model: String,
    val messages: List<MessageParam>
)

data class MessageParam(
    val role: String,
    val content: String
)

data class Choice(
    val index: Int,
    val message: MessageParam
)