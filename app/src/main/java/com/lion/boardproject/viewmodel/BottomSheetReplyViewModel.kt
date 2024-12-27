package com.lion.boardproject.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lion.boardproject.fragment.BottomSheetReplyFragment
import com.lion.boardproject.model.ReplyModel

class BottomSheetReplyViewModel(val bottomSheetReplyFragment: BottomSheetReplyFragment): ViewModel() {
    // editTextBoardReadReply - text
    val editTextBoardReadReplyText = MutableLiveData<String>()

    // list
    val replyListLive = MutableLiveData<MutableList<ReplyModel>>()
}