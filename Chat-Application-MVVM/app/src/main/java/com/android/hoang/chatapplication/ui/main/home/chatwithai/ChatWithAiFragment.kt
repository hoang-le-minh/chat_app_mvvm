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
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseFragment
import com.android.hoang.chatapplication.databinding.FragmentChatWithAiBinding
import com.android.hoang.chatapplication.ui.main.MainActivity
import com.android.hoang.chatapplication.util.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatWithAiFragment : BaseFragment<FragmentChatWithAiBinding>() {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentChatWithAiBinding.inflate(inflater, container, false)

    private val chatWithAiViewModel: ChatWithAiFragmentViewModel by viewModels()

    private var isChatGPT = true
    private var isLlama2 = false

    override fun prepareView(savedInstanceState: Bundle?) {
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
        chatWithAiViewModel.getChatResponse(binding.edtTypingMessage.text.toString())
        chatWithAiViewModel.message.observe(viewLifecycleOwner){
            when(it.status){
                Status.LOADING -> {}
                Status.SUCCESS -> {
                    Toast.makeText(requireContext(), it.data?.choices?.get(0)?.message?.content, Toast.LENGTH_SHORT).show()
                    binding.edtTypingMessage.setText("")
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    binding.edtTypingMessage.setText("")

                }
            }
        }
    }


    private fun initModelView(){
        if (isChatGPT){
            binding.txtChatGpt.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color_2))
            binding.txtLlama2.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
        } else {
            binding.txtLlama2.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color_2))
            binding.txtChatGpt.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
        }
    }

}