package com.lion.boardproject.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lion.boardproject.BoardActivity
import com.lion.boardproject.R
import com.lion.boardproject.databinding.FragmentBoardReadBinding
import com.lion.boardproject.databinding.ReplyItemListBinding
import com.lion.boardproject.model.BoardModel
import com.lion.boardproject.model.ReplyModel
import com.lion.boardproject.service.BoardService
import com.lion.boardproject.service.ReplyService
import com.lion.boardproject.service.UserService
import com.lion.boardproject.util.ReplyState
import com.lion.boardproject.util.UserState
import com.lion.boardproject.viewmodel.BoardReadViewModel
import com.lion.boardproject.viewmodel.ReplyItemListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat


class BoardReadFragment(val boardMainFragment: BoardMainFragment) : Fragment() {

    lateinit var fragmentBoardReadBinding: FragmentBoardReadBinding
    lateinit var boardActivity: BoardActivity

    // 현재 글의 문서 id를 담을 변수
    lateinit var boardDocumentId: String

    // 글 데이터를 담을 변수
    lateinit var boardModel: BoardModel

    // 댓글 데이터를 담을 변수 리스트
    val replyList = mutableListOf<ReplyModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentBoardReadBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_board_read, container, false)
        fragmentBoardReadBinding.boardReadViewModel = BoardReadViewModel(this)
        fragmentBoardReadBinding.lifecycleOwner = this
        boardActivity = activity as BoardActivity

        // 이미지 뷰를 안보이는 상태로 설정한다.
        fragmentBoardReadBinding.imageViewBoardRead.isVisible = false

        // 글 메서드 모음
        startBoardMethod()
        // 댓글 메서드 모음
        startReplyMethod()


        return fragmentBoardReadBinding.root
    }


    /*
    글
     */

    // 글 메서드
    private fun startBoardMethod() {
        // 툴바를 구성하는 메서드를 호출한다.
        settingToolbar()
        // arguments의 값을 변수에 담아주는 메서드를 호출한다.
        gettingArguments()
        // 글 데이터를 가져와 보여주는 메서드를 호출한다.
        settingBoardData()
    }

    // arguments의 값을 변수에 담아준다.
    fun gettingArguments(){
        boardDocumentId = arguments?.getString("boardDocumentId")!!
    }

    // 이전 화면으로 돌아가는 메서드
    fun movePrevFragment(){
        boardMainFragment.removeFragment(BoardSubFragmentName.BOARD_WRITE_FRAGMENT)
        boardMainFragment.removeFragment(BoardSubFragmentName.BOARD_READ_FRAGMENT)
    }

    // 툴바를 구성하는 메서드
    fun settingToolbar(){
        fragmentBoardReadBinding.apply {
            // 메뉴를 보이지 않게 설정한다.
            toolbarBoardRead.menu.children.forEach {
                if(it.itemId != R.id.menuItemBoardReadReply) {
                    it.isVisible = false
                }
            }

            toolbarBoardRead.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.menuItemBoardReadModify -> {
                        // 글의 문서 번호를 전달한다.
                        val dataBundle = Bundle()
                        dataBundle.putString("boardDocumentId", boardDocumentId)
                        boardMainFragment.replaceFragment(BoardSubFragmentName.BOARD_MODIFY_FRAGMENT, true, true, dataBundle)
                    }
                    R.id.menuItemBoardReadDelete -> {
                        val builder = MaterialAlertDialogBuilder(boardActivity)
                        builder.setTitle("글 삭제")
                        builder.setMessage("삭제시 복구할 수 없습니다")
                        builder.setNegativeButton("취소", null)
                        builder.setPositiveButton("삭제") { dialogInterface: DialogInterface, i: Int ->
                            proBoardDelete()
                        }
                        builder.show()
                    }
                    R.id.menuItemBoardReadReply -> {
                        // BottomSheetDialogFragment를 띄운다.
                        val bottomSheetFragment = BottomSheetReplyFragment(this@BoardReadFragment)
                        bottomSheetFragment.show(boardActivity.supportFragmentManager, "BottomSheetFragment")
                    }
                }
                true
            }
        }
    }

    // 글 데이터를 가져와 보여주는 메서드
    fun settingBoardData() {
        // 서버에서 데이터를 가져온다.
        CoroutineScope(Dispatchers.Main).launch {
            val work1 = async(Dispatchers.IO) {
                BoardService.selectBoardDataOneById(boardDocumentId)
            }
            boardModel = work1.await()

            fragmentBoardReadBinding.apply {
                boardReadViewModel?.textFieldBoardReadTitleText?.value = boardModel.boardTitle
                boardReadViewModel?.textFieldBoardReadTextText?.value = boardModel.boardText
                boardReadViewModel?.textFieldBoardReadTypeText?.value = boardModel.boardTypeValue.str
                boardReadViewModel?.textFieldBoardReadNickName?.value = boardModel.boardWriterNickName

                // 작성자와 로그인한 사람이 같으면 메뉴를 보기에 한다.
                if(boardModel.boardWriteId == boardActivity.loginUserDocumentId){
                    toolbarBoardRead.menu.children.forEach {
                        it.isVisible = true
                    }
                }
            }

            // 첨부 이미지가 있다면
            if(boardModel.boardFileName != "none") {
                // 여기다 넣으면 원래 아이콘 뜨다가 불러오면 사진이 뜸
                fragmentBoardReadBinding.imageViewBoardRead.isVisible = true
                val work1 = async(Dispatchers.IO) {
                    // 이미지에 접근할 수 있는 uri를 가져온다.
                    BoardService.gettingImage(boardModel.boardFileName)
                }
                val imageUri = work1.await()
                boardActivity.showServiceImage(imageUri, fragmentBoardReadBinding.imageViewBoardRead)
                // fragmentBoardReadBinding.imageViewBoardRead.isVisible = true
            }
        }
    }

    // 글 삭제 처리 메서드
    fun proBoardDelete() {
        CoroutineScope(Dispatchers.Main).launch {
            // 만약 첨부 이미지가 있다면 삭제한다.
            if(boardModel.boardFileName != "none") {
                val work1 = async(Dispatchers.IO) {
                    BoardService.removeImageFile(boardModel.boardFileName)
                }
                work1.join()
            }
            // 글 정보를 삭제한다.
            val work2 = async(Dispatchers.IO) {
                BoardService.deleteBoardData(boardDocumentId)
            }
            work2.join()
            // 글 목록 화면으로 이동한다.
            boardMainFragment.removeFragment(BoardSubFragmentName.BOARD_READ_FRAGMENT)
        }
    }

    /*
    댓글
     */

    // 댓글 메서드
    private fun startReplyMethod() {
        // RecyclerView 세팅
        settingRecyclerView()

    }

    // 저장 버튼 메서드
    fun saveButtonMethod() {
        fragmentBoardReadBinding.button.setOnClickListener {
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
            val replyText = fragmentBoardReadBinding.boardReadViewModel?.editTextBoardReadReplyText?.value!!
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

            fragmentBoardReadBinding.editTextBoardReadReply.text.clear()

            val work2 = async(Dispatchers.IO) {
                ReplyService.gettingReplyList(replyBoardId, replyState)
            }
            val dataList = work2.await()

            replyList.clear()

            dataList.forEach {
                replyList.add(it)
            }

            fragmentBoardReadBinding.boardReadViewModel?.textViewReplyText?.value = "댓글 ${replyList.size}"
            fragmentBoardReadBinding.recyclerViewRead.adapter?.notifyDataSetChanged()
        }
    }

    // 리사이클러 세팅
    private fun settingRecyclerView() {
        fragmentBoardReadBinding.apply {
            recyclerViewRead.adapter = RecyclerViewReadAdapter()
            settingFirstReplyData()
        }
    }

    // 댓글 데이터 불러오기
    fun settingFirstReplyData() {
        replyList.clear()
        val replyState = ReplyState.REPLY_STATE_NORMAL
        CoroutineScope(Dispatchers.Main).launch {
            val work2 = async(Dispatchers.IO) {
                ReplyService.gettingReplyList(boardDocumentId, replyState)
            }
            val dataList = work2.await()

            replyList.addAll(dataList)

            fragmentBoardReadBinding.boardReadViewModel?.textViewReplyText?.value = "댓글 ${replyList.size}"
            fragmentBoardReadBinding.recyclerViewRead.adapter?.notifyDataSetChanged()
        }
    }
    // 날짜 보여주는 메서드
    private fun showDateData(timeData: Long): String {
        val dataFormat1 = SimpleDateFormat("yyyy-MM-dd. HH:mm:ss")
        val date = dataFormat1.format(timeData)
        return date
    }


    // 리사이클러 어뎁터
    inner class RecyclerViewReadAdapter: RecyclerView.Adapter<RecyclerViewReadAdapter.ViewHolderRead>() {
        inner class ViewHolderRead(val replyItemListBinding: ReplyItemListBinding): RecyclerView.ViewHolder(replyItemListBinding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRead {
            val replyItemListBinding = DataBindingUtil.inflate<ReplyItemListBinding>(layoutInflater, R.layout.reply_item_list, parent, false)
            replyItemListBinding.replyItemListViewModel = ReplyItemListViewModel()
            replyItemListBinding.lifecycleOwner = this@BoardReadFragment
            val viewHolderRead = ViewHolderRead(replyItemListBinding)

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

        override fun onBindViewHolder(holder: ViewHolderRead, position: Int) {
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