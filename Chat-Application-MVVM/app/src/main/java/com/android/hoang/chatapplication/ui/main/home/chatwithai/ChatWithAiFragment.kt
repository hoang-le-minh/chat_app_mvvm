package com.android.hoang.chatapplication.ui.main.home.chatwithai

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseFragment
import com.android.hoang.chatapplication.data.remote.model.Message
import com.android.hoang.chatapplication.databinding.FragmentChatWithAiBinding
import com.android.hoang.chatapplication.ui.main.MainActivity
import com.android.hoang.chatapplication.util.Constants.CHAT_BOT_ID
import com.android.hoang.chatapplication.util.Constants.LOG_TAG
import com.android.hoang.chatapplication.util.Constants.MESSAGE_TYPE_STRING
import com.android.hoang.chatapplication.util.Status
import com.easy.translator.EasyTranslator
import com.easy.translator.LanguagesModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ChatWithAiFragment : BaseFragment<FragmentChatWithAiBinding>() {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentChatWithAiBinding.inflate(inflater, container, false)

    private val chatWithAiViewModel: ChatWithAiFragmentViewModel by viewModels()
    private lateinit var chatWithAIAdapter: ChatWithAIAdapter
    private lateinit var messageList: MutableList<Message>

    private var isGemini = true
    private var isLlama2 = false

    private var translateResult = ""

    override fun prepareView(savedInstanceState: Bundle?) {
        messageList = mutableListOf()
        chatWithAIAdapter = ChatWithAIAdapter(messageList)
        initModelView()
        binding.txtChatGpt.setOnClickListener {
            isGemini = true
            isLlama2 = false
            initModelView()
        }
        binding.txtLlama2.setOnClickListener {
            isGemini = false
            isLlama2 = true
            initModelView()
        }

        binding.btnBack.setOnClickListener {
            (activity as MainActivity).hideAIChat()
        }

        binding.btnSendMessage.setOnClickListener {
            if (binding.edtTypingMessage.text.isNotEmpty()){
                sendMessage()
            }
        }
        observeModel()

        updateUiSendBtn()
    }

    private fun updateUiSendBtn(){
        binding.chatBotViewModel = chatWithAiViewModel
        binding.lifecycleOwner = this
        chatWithAiViewModel.question.observe(viewLifecycleOwner){
            chatWithAiViewModel.updateSendButtonState()
        }
    }

    private fun sendMessage() {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return

//        // CHAT GPT
//        chatWithAiViewModel.getChatResponse(binding.edtTypingMessage.text.toString().trim())

        // GEMINI AI
        if (isGemini) {
            chatWithAiViewModel.getGeminiResponse(binding.edtTypingMessage.text.toString().trim())
            Log.d(LOG_TAG, "sendMessage: Gemini")
        }
        if (isLlama2){
            // Llama2
            chatWithAiViewModel.getLlama2Response(binding.edtTypingMessage.text.toString().trim())
            Log.d(LOG_TAG, "sendMessage: Llama2")

            val sharedPref = requireContext().getSharedPreferences("llama2", Context.MODE_PRIVATE)
            sharedPref.edit().putString("llama2", binding.edtTypingMessage.text.toString().trim()).apply()
        }

        addMessageToRecyclerView(binding.edtTypingMessage.text.toString(), currentUser.uid)
        binding.edtTypingMessage.setText("")

    }

    private fun observeModel(){

//        // CHAT GPT
//        chatWithAiViewModel.message.observe(viewLifecycleOwner){
//            when(it.status){
//                Status.LOADING -> {}
//                Status.SUCCESS -> {
////                    Toast.makeText(requireContext(), it.data?.choices?.get(0)?.message?.content, Toast.LENGTH_SHORT).show()
//                    it.data?.choices?.get(0)?.message?.content?.let { it1 ->
//                        if (messageList.last().message != it1){
//                        addMessageToRecyclerView(
//                            it1,
//                            CHAT_BOT_ID)}
//                    }
////                    chatWithAiViewModel.clearData()
//                }
//                Status.ERROR -> {
//                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
//                    binding.edtTypingMessage.setText("")
//
//                }
//            }
//        }

        // GEMINI AI
        chatWithAiViewModel.messageGemini.observe(viewLifecycleOwner){
            when(it.status){
                Status.LOADING -> {
                    addMessageToRecyclerView("Generating...", CHAT_BOT_ID)
                }
                Status.SUCCESS -> {
                    messageList.removeAt(messageList.size - 1)
                    chatWithAIAdapter.notifyItemRemoved(messageList.size - 1)
//                    Toast.makeText(requireContext(), it.data?.choices?.get(0)?.message?.content, Toast.LENGTH_SHORT).show()
                    it.data?.prompt?.let { it1 ->
                        if (messageList.last().message != it1){
                            addMessageToRecyclerView(
                                it1,
                                CHAT_BOT_ID)}
                    }
//                    chatWithAiViewModel.clearData()
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    binding.edtTypingMessage.setText("")

                }
            }
        }

        chatWithAiViewModel.messageLlama2.observe(viewLifecycleOwner){
            when(it.status){
                Status.LOADING -> {
                    addMessageToRecyclerView("Generating...", CHAT_BOT_ID)
                }
                Status.SUCCESS -> {
                    messageList.removeAt(messageList.size - 1)
                    chatWithAIAdapter.notifyItemRemoved(messageList.size - 1)

                    val sharedPrefLang = requireContext().getSharedPreferences("current_language", Context.MODE_PRIVATE)
                    val currentLang = sharedPrefLang.getString("current_language", "")

                    if (currentLang == "vi"){
                        // Translate
                        EasyTranslator(requireContext()).translate(
                            text = decodeBase64(it.data.toString()),
                            LanguagesModel.AUTO_DETECT,
                            LanguagesModel.VIETNAMESE,
                            { result ->
                                Log.d(LOG_TAG, "translate: $result")
                                addMessageToRecyclerView(
                                    result,
                                    CHAT_BOT_ID)
                            },
                            { error ->
                                Log.d(LOG_TAG, "translateError: $error")
                                addMessageToRecyclerView(
                                    decodeBase64(it.data.toString()),
                                    CHAT_BOT_ID)
                            }
                        )

                    } else {
                        addMessageToRecyclerView(
                            decodeBase64(it.data.toString()),
                            CHAT_BOT_ID)
                    }

                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(LOG_TAG, "llama2Error: ${it.message}")
                }
            }
        }
    }

    private fun addMessageToRecyclerView(strMessage: String, senderId: String){
        val message = Message(senderId, "", strMessage, MESSAGE_TYPE_STRING.toString(), SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(Date()),"false")
        messageList.add(message)
        chatWithAIAdapter.notifyDataSetChanged()
        binding.recyclerViewChat.smoothScrollToPosition(chatWithAIAdapter.itemCount)
    }

    private fun removeMessageToRecyclerView(strMessage: String, senderId: String){
        val message = Message(senderId, "", strMessage, MESSAGE_TYPE_STRING.toString(), SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(Date()),"false")
        messageList.removeAt(messageList.size - 1)
        chatWithAIAdapter.notifyItemRemoved(messageList.size - 1)
        binding.recyclerViewChat.smoothScrollToPosition(chatWithAIAdapter.itemCount)
    }

    private fun initModelView(){
        if (isGemini){
            binding.txtChatGpt.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color_2))
            binding.txtLlama2.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
        } else {
            binding.txtLlama2.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color_2))
            binding.txtChatGpt.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
        }

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true
        binding.recyclerViewChat.layoutManager = layoutManager
        binding.recyclerViewChat.adapter = chatWithAIAdapter
    }

    private fun decodeBase64(encodedString: String): String {
        // Decode the Base64 string to a byte array
        val decodedBytes = Base64.decode(encodedString, Base64.DEFAULT)

        // Convert the byte array to a string
        val decodedString = String(decodedBytes, Charsets.UTF_8)
        val sharedPref = requireContext().getSharedPreferences("llama2", Context.MODE_PRIVATE)
        val currentPrompt = sharedPref.getString("llama2", "") ?: ""

        return decodedString.replace(currentPrompt, "")
    }
}