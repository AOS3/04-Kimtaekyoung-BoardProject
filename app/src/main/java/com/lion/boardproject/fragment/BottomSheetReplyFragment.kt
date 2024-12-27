package com.lion.boardproject.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lion.boardproject.BoardActivity
import com.lion.boardproject.R
import com.lion.boardproject.databinding.FragmentBottomSheetReplyBinding
import com.lion.boardproject.databinding.ReplyItemListBinding
import com.lion.boardproject.model.ReplyModel
import com.lion.boardproject.service.ReplyService
import com.lion.boardproject.util.ReplyState
import com.lion.boardproject.viewmodel.BottomSheetReplyViewModel
import com.lion.boardproject.viewmodel.ReplyItemListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat


class BottomSheetReplyFragment(val boardReadFragment: BoardReadFragment) : BottomSheetDialogFragment() {

    lateinit var bottomReplyBinding: FragmentBottomSheetReplyBinding
    lateinit var boardActivity: BoardActivity

    // 댓글 데이터를 담을 변수 리스트
    private val replyList = mutableListOf<ReplyModel>()

    private var boardDocumentId: String = boardReadFragment.boardDocumentId

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bottomReplyBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_sheet_reply, container, false)
        bottomReplyBinding.bottomSheetReplyViewModel = BottomSheetReplyViewModel(this)
        bottomReplyBinding.lifecycleOwner = this@BottomSheetReplyFragment
        boardActivity = activity as BoardActivity
        startReplyMethod()
        updateData()

        bottomReplyBinding.recyclerviewBottomReply.post {
            Log.d("BottomSheet", "RecyclerView 높이: ${bottomReplyBinding.recyclerviewBottomReply.height}")
        }

        return bottomReplyBinding.root
    }



    /*
    댓글
     */

    // 댓글 메서드
    private fun startReplyMethod() {
        // RecyclerView 세팅
        settingRecyclerView()

        // 임시 버튼
        bottomReplyBinding.button.setOnClickListener {
            saveReplyData()
            boardActivity.hideSoftInput()
        }
    }

    // 댓글 입력 처리
    private fun saveReplyData() {
        CoroutineScope(Dispatchers.Main).launch {

            // 댓글 작성자 닉네임
            val replyNickName = boardActivity.loginUserNickName
            // 댓글 내용
            val replyText = bottomReplyBinding.bottomSheetReplyViewModel?.editTextBoardReadReplyText?.value!!
            // 댓글이 달린 글 구분 값
            val replyBoardId = boardDocumentId
            // 시간
            val replyTimeStamp = System.currentTimeMillis()
            // 댓글 상태값
            val replyState = ReplyState.REPLY_STATE_NORMAL

            val replyModel = ReplyModel().also {
                it.replyNickName = replyNickName
                it.replyText = replyText
                it.replyBoardId = replyBoardId
                it.replyTimeStamp = replyTimeStamp
            }
            val work1 = async(Dispatchers.IO) {
                ReplyService.addReplyData(replyModel)
            }
            work1.join()

            bottomReplyBinding.editTextBottomReadReply.text.clear()

            val work2 = async(Dispatchers.IO) {
                ReplyService.gettingReplyList(replyBoardId, replyState)
            }
            val dataList = work2.await()

            replyList.clear()

            replyList.addAll(dataList)
            bottomReplyBinding.bottomSheetReplyViewModel?.replyListLive?.value = replyList

            bottomReplyBinding.recyclerviewBottomReply.adapter?.notifyDataSetChanged()
        }
    }

    // 리사이클러 세팅
    private fun settingRecyclerView() {
        bottomReplyBinding.apply {
            recyclerviewBottomReply.adapter = RecyclerViewBottomReadAdapter()
            settingFirstReplyData()
        }
    }

    // 댓글 데이터 불러오기
    private fun settingFirstReplyData() {
        replyList.clear()
        val replyState = ReplyState.REPLY_STATE_NORMAL
        CoroutineScope(Dispatchers.Main).launch {
            val work2 = async(Dispatchers.IO) {
                ReplyService.gettingReplyList(boardDocumentId, replyState)
            }
            val dataList = work2.await()

            replyList.addAll(dataList)
            bottomReplyBinding.bottomSheetReplyViewModel?.replyListLive?.value = replyList

            bottomReplyBinding.recyclerviewBottomReply.adapter?.notifyDataSetChanged()
        }
    }

    // 날짜 보여주는 메서드
    private fun showDateData(timeData: Long): String {
        val dataFormat1 = SimpleDateFormat("yyyy-MM-dd. HH:mm:ss")
        val date = dataFormat1.format(timeData)
        return date
    }

    private fun updateData() {
        bottomReplyBinding.bottomSheetReplyViewModel?.replyListLive?.observe(viewLifecycleOwner) {
            boardReadFragment.settingFirstReplyData()
        }
    }



    // 리사이클러 어뎁터
    inner class RecyclerViewBottomReadAdapter: RecyclerView.Adapter<RecyclerViewBottomReadAdapter.ViewHolderBottomRead>() {
        inner class ViewHolderBottomRead(val replyItemListBinding: ReplyItemListBinding): RecyclerView.ViewHolder(replyItemListBinding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBottomRead {
            val replyItemListBinding = DataBindingUtil.inflate<ReplyItemListBinding>(layoutInflater, R.layout.reply_item_list, parent, false)
            replyItemListBinding.replyItemListViewModel = ReplyItemListViewModel()
            replyItemListBinding.lifecycleOwner = this@BottomSheetReplyFragment
            val viewHolderRead = ViewHolderBottomRead(replyItemListBinding)

            replyItemListBinding.buttonReplyDelete.setOnClickListener {
                val builder = MaterialAlertDialogBuilder(boardActivity)
                builder.setTitle("댓글 삭제")
                builder.setMessage("삭제한 댓글은 볼 수 없습니다")
                builder.setNegativeButton("취소", null)
                builder.setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                    CoroutineScope(Dispatchers.Main).launch {
                        val work1 = async(Dispatchers.IO) {
                            ReplyService.updateReplyState(
                                replyList[viewHolderRead.adapterPosition].replyDocumentId,
                                ReplyState.REPLY_STATE_DELETE
                            )
                        }
                        work1.join()
                        settingFirstReplyData()
                    }
                }
                builder.show()
            }
            return viewHolderRead
        }

        override fun getItemCount(): Int {
            return replyList.size
        }

        override fun onBindViewHolder(holder: ViewHolderBottomRead, position: Int) {
            holder.replyItemListBinding.replyItemListViewModel?.textViewReplyNickNameText?.value = replyList[position].replyNickName
            holder.replyItemListBinding.replyItemListViewModel?.textViewReplyTextText?.value = replyList[position].replyText
            val date = showDateData(replyList[position].replyTimeStamp)
            holder.replyItemListBinding.replyItemListViewModel?.textViewReplyTimeText?.value = date

            if (replyList[position].replyNickName == boardActivity.loginUserNickName) {
                holder.replyItemListBinding.buttonReplyDelete.isVisible = true
            }
        }
    }
}