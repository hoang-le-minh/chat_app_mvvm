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

data class MessageResponse2(
    @SerializedName("id")
    @Expose
    val id: String? = null,
    @SerializedName("object")
    @Expose
    val strObject: String? = null,
    @SerializedName("choices")
    @Expose
    val choices: List<Choice2>? = null,
)

data class MessageParamPost(
    var model: String,
    val messages: List<MessageParam>
)

data class MessageParamPost2(
    var model: String,
    var prompt: String,
    var max_token: Int,
    var temperature: Int
)

data class MessageParam(
    val role: String,
    val content: String
)

data class Choice(
    val index: Int,
    val message: MessageParam
)

data class Choice2(
    val text: String,
    val index: Int
)