package com.android.hoang.chatapplication.data.remote.service

import com.google.gson.annotations.SerializedName
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TranslateService {
    @FormUrlEncoded
    @POST("language/translate/v2")
    suspend fun translateText(
        @Field("q") text: String,
        @Field("target") target: String
    ): TranslationResponse
}

data class TranslationResponse(
    @SerializedName("data") val data: TranslationData
)

data class TranslationData(
    @SerializedName("translations") val translations: List<Translation>
)

data class Translation(
    @SerializedName("translatedText") val translatedText: String
)