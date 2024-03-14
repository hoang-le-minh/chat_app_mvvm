package com.android.hoang.chatapplication.ui.main.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseFragment
import com.android.hoang.chatapplication.data.remote.model.*
import com.android.hoang.chatapplication.databinding.FragmentHomeBinding
import com.android.hoang.chatapplication.ui.main.MainActivity
import com.android.hoang.chatapplication.ui.main.MainActivityViewModel
import com.android.hoang.chatapplication.ui.main.friend.SearchUserAdapter
import com.android.hoang.chatapplication.util.Constants
import com.android.hoang.chatapplication.util.Constants.LOG_TAG
import com.android.hoang.chatapplication.util.Constants.MESSAGE_TYPE_STRING
import com.android.hoang.chatapplication.util.Status
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    //region vars
    private val viewModel: HomeFragmentViewModel by viewModels()
    private val mainViewModel: MainActivityViewModel by activityViewModels()
    private val listMessageAdapter = ListUserMessageAdapter()
    private lateinit var searchAdapter: SearchMessageAdapter
    private var isSearch = false
    private val receiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            observeModel()
        }

    }
    //endregion

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater, container, false)

    override fun prepareView(savedInstanceState: Bundle?) {
        LogUtils.d("$this prepareView")

        searchAdapter = SearchMessageAdapter()
        this.observeModel()
//        val activity = activity as MainActivity
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            val window: Window = activity.window
//            window.statusBarColor = context?.let { ContextCompat.getColor(it, R.color.color_1) }!!
//        }

        val filter = IntentFilter("com.android.hoang.chatapplication.UPDATE_HOME_UI")
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, filter)

        binding.searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                showSearchView()
                binding.txtChatWithAi.visibility = View.GONE
            }
        }
        binding.btnCancelSearch.setOnClickListener {
            cancelSearch()
        }

        binding.txtChatWithAi.setOnClickListener {
            (activity as MainActivity).showAIChat()
        }

        onBackPressCallBack()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)
    }

    /**
     * Observe LiveData models
     */
    private fun observeModel() {
        if (view != null) {
            viewModel.getConversationList()
            viewModel.conversationList.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { listUserId ->
                            prepareComponents(listUserId)
                            searchMessage(listUserId)
                            binding.btnCreateNewMessage.setOnClickListener {
                                goToCreateConversationFragment(ListString(listUserId))
                            }
                        }
                    }
                    Status.LOADING -> {
                        LogUtils.d("$this LOADING")
//                    showLoading()
                    }
                    Status.ERROR -> {
                        LogUtils.d("$this ERROR")
                        hideLoading()
                    }
                }
            }
        }
    }

    private fun showSearchView(){
        binding.searchRecyclerView.visibility = View.VISIBLE
        binding.recyclerviewMessage.visibility = View.GONE
        binding.btnCancelSearch.visibility = View.VISIBLE
        isSearch = true
    }

    private fun cancelSearch(){
        binding.searchRecyclerView.visibility = View.GONE
        binding.recyclerviewMessage.visibility = View.VISIBLE
        binding.btnCancelSearch.visibility = View.GONE
        binding.noMatchingResult.visibility = View.GONE

        binding.searchView.setQuery("", false)
        binding.searchView.clearFocus()
        isSearch = false


        binding.txtChatWithAi.visibility = View.VISIBLE
    }

    private fun searchMessage(listUserId: MutableList<String>) {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText, listUserId)

                return true
            }

        })
    }

    // search in exist conversation
    private fun filterList(s: String?, listId: MutableList<String>){
        if (s != null) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val myRef = FirebaseDatabase.getInstance().getReference("messages")
            val searchList = mutableListOf<SearchMessage>()
            if (currentUser != null) {
                myRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        searchList.clear()
                        for (userId: String in listId){
                            var count = 0
                            for (dataSnapShot: DataSnapshot in snapshot.children) {
                                val message = dataSnapShot.getValue(Message::class.java) ?: continue
                                if ((((message.senderId == currentUser.uid) && (message.receiverId == userId)) ||
                                            ((message.receiverId == currentUser.uid) && (message.senderId == userId))) &&
                                    (message.type == MESSAGE_TYPE_STRING.toString()) && message.message.lowercase()
                                        .contains(s.lowercase())
                                ){
                                    count++
                                }
                            }
                            if(count > 0) searchList.add(SearchMessage(userId, count))
                        }

//                        Log.d(Constants.LOG_TAG, "onDataChange: $searchList")
                        updateSearchRecyclerView(searchList)

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
            }
        }
    }

    private fun updateSearchRecyclerView(searchList: List<SearchMessage>) {
        binding.searchRecyclerView.adapter = searchAdapter
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchAdapter.submitList(searchList)
        if(searchList.isEmpty()){
            binding.noMatchingResult.visibility = View.VISIBLE
        } else {
            binding.noMatchingResult.visibility = View.GONE
        }
    }

    private fun goToCreateConversationFragment(list: ListString){
        val action = HomeFragmentDirections.actionHomeFragmentToCreateConversationFragment(list)
        findNavController().navigate(action)
    }

    /**
     * Prepare components & show data in UI
     *
     */
    private fun prepareComponents(listId: MutableList<String>) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val recyclerView = binding.recyclerviewMessage
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.getLatestMessageList(listId)
        viewModel.latestMessageList.observe(viewLifecycleOwner){ it ->
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { list ->
                        // unread user count
                        var count = 0
                        for (message: Message in list){
                            if (!message.isSeen.toBoolean() && message.receiverId == currentUser?.uid){
                                Log.d(LOG_TAG, "latestMessageUnread: $message")
                                count++
                            }
                        }
                        mainViewModel.updateUnreadUserCount(count)

                        listMessageAdapter.submitList(list)
                        recyclerView.adapter = listMessageAdapter
                    }
                    hideLoading()
                }
                Status.LOADING -> {
                    LogUtils.d("$this LOADING")
//                    showLoading()
                }
                Status.ERROR -> {
                    LogUtils.d("$this ERROR")
                    hideLoading()
                }
            }
        }


    }

    private fun onBackPressCallBack(){
        // Set up callback for the back button press
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if ((activity as MainActivity).isAIChat){
                    (activity as MainActivity).hideAIChat()
                } else if (isSearch){
                    cancelSearch()
                } else
                    showExitDialog()
            }
        }
        // Add the callback to the OnBackPressedDispatcher
        activity?.onBackPressedDispatcher?.addCallback(this, callback)
    }

    private fun showExitDialog(){
        context?.let {
            MaterialDialog(it).show {
                cancelable(true)
                cancelOnTouchOutside(true)
                message(text = getString(R.string.question_exit_app))
                positiveButton(R.string.ok) {
                    activity?.finish()
                }
                negativeButton(R.string.cancel){
                    dismiss()
                }
            }
        }
    }

}