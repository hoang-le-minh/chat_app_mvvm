package com.android.hoang.chatapplication.domain.repository

import com.android.hoang.chatapplication.data.remote.service.TranslationResponse
import com.android.hoang.chatapplication.util.Resource

interface TranslateRepository {
    suspend fun translateText(text: String, target: String): TranslationResponse
}