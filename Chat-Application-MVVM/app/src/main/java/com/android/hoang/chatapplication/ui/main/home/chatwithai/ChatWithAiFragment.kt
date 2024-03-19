package com.android.hoang.chatapplication.ui.main.home.chatwithai

import android.content.res.ColorStateList
import android.os.Bundle
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
import com.android.hoang.chatapplication.util.Constants.MESSAGE_TYPE_STRING
import com.android.hoang.chatapplication.util.Status
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

    private var isChatGPT = true
    private var isLlama2 = false

    override fun prepareView(savedInstanceState: Bundle?) {
        messageList = mutableListOf()
        chatWithAIAdapter = ChatWithAIAdapter(messageList)
        initModelView()
        binding.txtChatGpt.setOnClickListener {
            isChatGPT = true
            isLlama2 = false
            initModelView()
        }
        binding.txtLlama2.setOnClickListener {
            isChatGPT = false
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
        chatWithAiViewModel.getChatResponse(binding.edtTypingMessage.text.toString().trim())
        addMessageToRecyclerView(binding.edtTypingMessage.text.toString(), currentUser.uid)
        binding.edtTypingMessage.setText("")

    }

    private fun observeModel(){
        chatWithAiViewModel.message.observe(viewLifecycleOwner){
            when(it.status){
                Status.LOADING -> {}
                Status.SUCCESS -> {
//                    Toast.makeText(requireContext(), it.data?.choices?.get(0)?.message?.content, Toast.LENGTH_SHORT).show()
                    it.data?.choices?.get(0)?.message?.content?.let { it1 ->
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
    }

    private fun addMessageToRecyclerView(strMessage: String, senderId: String){
        val message = Message(senderId, "", strMessage, MESSAGE_TYPE_STRING.toString(), SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(Date()),"false")
        messageList.add(message)
        chatWithAIAdapter.notifyDataSetChanged()
        binding.recyclerViewChat.smoothScrollToPosition(chatWithAIAdapter.itemCount)
    }

    private fun initModelView(){
        if (isChatGPT){
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

}