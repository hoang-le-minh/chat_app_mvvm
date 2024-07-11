package com.android.hoang.chatapplication.domain.usecase

import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.domain.repository.TranslateRepository
import com.android.hoang.chatapplication.util.State
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TranslateUseCase @Inject constructor(private val translateRepository: TranslateRepository) {
    suspend fun invokeTranslate(text: String, target: String) = flow<State<String>> {
        try {
            emit(State.Loading())
            val res = translateRepository.translateText(text, target)
            val result = res.data.translations.firstOrNull()?.translatedText
            if (!result.isNullOrBlank()) {
                emit(State.Success(result))

            } else {
                emit(State.Error(StringUtils.getString(R.string.something_went_wrong)))
            }
        } catch (e: Exception){
            LogUtils.d("$this ${e.localizedMessage}")
            emit(State.Error(e.localizedMessage))
        }
    }
}