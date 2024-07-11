package com.android.hoang.chatapplication.data.repository

import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.data.remote.model.*
import com.android.hoang.chatapplication.data.remote.service.GPTService
import com.android.hoang.chatapplication.data.remote.service.Llama2Service
import com.android.hoang.chatapplication.data.remote.service.PromptLlama2
import com.android.hoang.chatapplication.domain.repository.ChatBotRepository
import com.android.hoang.chatapplication.util.Constants
import com.blankj.utilcode.util.StringUtils
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.ResponseStoppedException
import retrofit2.Response
import javax.inject.Inject

class ChatBotRepositoryImpl @Inject constructor(private val gptService: GPTService, private val llama2Service: Llama2Service): ChatBotRepository {
    override suspend fun getChatResponse(messageParamPost: MessageParamPost): Response<MessageResponse> = gptService.getResponseChat(messageParamPost)

    override suspend fun getChatResponse2(messageParamPost: MessageParamPost2): Response<MessageResponse2> = gptService.getResponseChat2(messageParamPost)
    override suspend fun getGeminiResponse(prompt: String): GeminiChat {
        val generativeModel = GenerativeModel(
            // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
            modelName = "gemini-1.5-flash",
            // Access your API key as a Build Configuration variable (see "Set up your API key" above)
            apiKey = Constants.GEMINI_API_KEY
        )

        return try {
            val response = generativeModel.generateContent(prompt)
            GeminiChat(
                prompt = response.text ?: StringUtils.getString(R.string.error),
                isFromUser = false
            )
        } catch (e: ResponseStoppedException){
            GeminiChat(
                prompt = e.message ?: StringUtils.getString(R.string.error),
                isFromUser = false
            )
        }

    }

    override suspend fun generativeText(prompt: PromptLlama2): Response<List<String>> = llama2Service.generativeText(prompt)
}