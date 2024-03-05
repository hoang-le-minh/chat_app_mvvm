package com.android.hoang.chatapplication.ui.main.friend.friendviewpager.friendrequest

import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.hoang.chatapplication.base.BaseFragment
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.databinding.FragmentFriendRequestBinding
import com.android.hoang.chatapplication.ui.main.MainActivity
import com.android.hoang.chatapplication.ui.main.MainActivityViewModel
import com.android.hoang.chatapplication.util.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendRequestFragment : BaseFragment<FragmentFriendRequestBinding>() {

    private val friendRequestViewModel: FriendRequestFragmentViewModel by viewModels()
    private val mainViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var requestAdapter: FriendRequestAdapter
    private lateinit var sentAdapter: FriendRequestAdapter

    override fun prepareView(savedInstanceState: Bundle?) {
        requestAdapter = FriendRequestAdapter(mainViewModel, friendRequestViewModel, true)
        sentAdapter = FriendRequestAdapter(mainViewModel, friendRequestViewModel, false)
        observerModel()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentFriendRequestBinding.inflate(inflater, container, false)

    private fun observerModel(){
        // request list
        friendRequestViewModel.getRequestIdList()
        friendRequestViewModel.listIdRequest.observe(viewLifecycleOwner){
            when(it.status){
                Status.LOADING -> {
//                    showLoading()
                }
                Status.SUCCESS -> {
                    it.data?.let { list ->
//                        Log.d(Constants.LOG_TAG, "friendRequest.initListRequestUser: ${list[0]}")
                        initListRequest(list)
                        mainViewModel.setRequestCount(list.size)
                    }
//                    hideLoading()
                }
                Status.ERROR -> {
                    it.message.let { msg ->
//                        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                        mainViewModel.setRequestCount(0)
//                        hideLoading()
                    }
                }
            }
        }

        // sent list
        friendRequestViewModel.getSentIdList()
        friendRequestViewModel.listIdSent.observe(viewLifecycleOwner){
            when(it.status){
                Status.LOADING -> {
//                    showLoading()
                }
                Status.SUCCESS -> {
                    it.data?.let { list ->

                        initListSent(list)
                    }
//                    hideLoading()
                }
                Status.ERROR -> {
                    it.message.let { msg ->
//                        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
//                        hideLoading()
                    }
                }
            }
        }

        mainViewModel.updateRequestStatus.observe(viewLifecycleOwner){
            if (it){
                friendRequestViewModel.getRequestIdList()
                friendRequestViewModel.getSentIdList()
                mainViewModel.updateRequestStatus(false)
            }
        }

    }

    private fun initListRequest(list: MutableList<String>){
        friendRequestViewModel.getRequestListByListId(list)
        friendRequestViewModel.listRequest.observe(viewLifecycleOwner){
            when(it.status){
                Status.LOADING -> {
//                    showLoading()
                }
                Status.SUCCESS -> {
                    it.data?.let { list ->
//                        Log.d(Constants.LOG_TAG, "friendRequest.initListRequest: ${list[0]}")

                        initRequestRecyclerView(list)
                    }
//                    hideLoading()
                }
                Status.ERROR -> {
                    it.message.let { msg ->
//                        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                        initRequestRecyclerView(mutableListOf())
//                        hideLoading()
                    }
                }
            }
        }
    }

    private fun initRequestRecyclerView(requestList: MutableList<UserFirebase>){
        val recyclerView = binding.requestRecyclerView

        friendRequestViewModel.resultAccept.observe(viewLifecycleOwner){
//            if (it.status == Status.SUCCESS){
//                adapter.setOnButtonClickListener(object : FriendRequestAdapter.OnButtonClickListener{
//                    override fun onButtonClick(position: Int) {
//                        requestList.removeAt(position)
//                        adapter.notifyItemRemoved(position)
//                    }
//
//                })
//                adapter.submitList(requestList)
//            }
        }
        requestAdapter.submitList(requestList)
        recyclerView.adapter = requestAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        setItemTouchHelper()
    }

    private fun setItemTouchHelper() {
        ItemTouchHelper(object : ItemTouchHelper.Callback(){

            private val limitScrollX = dipToPx(90f, activity as MainActivity)
            private var currentScrollX = 0
            private var currentScrollXWhenInActive = 0
            private var initXWhenInActive = 0f
            private var firstInActive = false

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = 0
                val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            }

            override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                return Integer.MAX_VALUE.toFloat()
            }

            override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
                return Integer.MAX_VALUE.toFloat()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
                    if (dX == 0f){
                        currentScrollX = viewHolder.itemView.scrollX
                        firstInActive = true
                    }

                    if (isCurrentlyActive){
                        // swipe with finger
                        var scrollOffset = currentScrollX + (-dX).toInt()
                        if (scrollOffset > limitScrollX){
                            scrollOffset = limitScrollX
                        } else if (scrollOffset < 0){
                            scrollOffset = 0
                        }

                        viewHolder.itemView.scrollTo(scrollOffset, 0)
                    } else{
                        if (firstInActive){
                            firstInActive = false
                            currentScrollXWhenInActive = viewHolder.itemView.scrollX
                            initXWhenInActive = dX
                        }

                        if (viewHolder.itemView.scrollX < limitScrollX){
                            viewHolder.itemView.scrollTo((currentScrollXWhenInActive * dX / initXWhenInActive).toInt(), 0)
                        }
                    }
                }

            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)

                if (viewHolder.itemView.scrollX > limitScrollX){
                    viewHolder.itemView.scrollTo(limitScrollX, 0)
                } else if (viewHolder.itemView.scrollX < 0){
                    viewHolder.itemView.scrollTo(0, 0)
                }
            }

        }).apply {
            attachToRecyclerView(binding.requestRecyclerView)
        }
    }

    private fun dipToPx(dipValue: Float, context: Context): Int{
        return (dipValue * context.resources.displayMetrics.density).toInt()
    }

    private fun initListSent(list: MutableList<String>){
        friendRequestViewModel.getSentListByListId(list)
        friendRequestViewModel.listSent.observe(viewLifecycleOwner){
            when(it.status){
                Status.LOADING -> {
//                    showLoading()
                }
                Status.SUCCESS -> {
                    it.data?.let { list ->
//                        Log.d(Constants.LOG_TAG, "friendRequest.initListSent: ${list[0]}")

                        initSentRecyclerView(list)
                    }
//                    hideLoading()
                }
                Status.ERROR -> {
                    it.message.let { msg ->
//                        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                        initSentRecyclerView(mutableListOf())
//                        hideLoading()
                    }
                }
            }
        }

    }

    private fun initSentRecyclerView(sentList: MutableList<UserFirebase>){
        val recyclerView = binding.sentRecyclerView

        friendRequestViewModel.resultCancelRequest.observe(viewLifecycleOwner){
//            if (it.status == Status.SUCCESS){
//                adapter.setOnButtonClickListener(object : FriendRequestAdapter.OnButtonClickListener{
//                    override fun onButtonClick(position: Int) {
//                        sentList.removeAt(position)
//                        adapter.notifyItemRemoved(position)
//                    }
//
//                })
//                adapter.submitList(sentList)
//            }
        }
        sentAdapter.submitList(sentList)
        recyclerView.adapter = sentAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

}