package com.android.hoang.chatapplication.data.repository

import com.android.hoang.chatapplication.data.remote.service.TranslateService
import com.android.hoang.chatapplication.data.remote.service.TranslationResponse
import com.android.hoang.chatapplication.domain.repository.TranslateRepository
import javax.inject.Inject

class TranslateRepositoryImpl @Inject constructor(private val translateService: TranslateService): TranslateRepository {
    override suspend fun translateText(text: String, target: String): TranslationResponse = translateService.translateText(text, target)
}