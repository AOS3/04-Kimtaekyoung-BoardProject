package com.lion.boardproject.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lion.boardproject.fragment.BoardReadFragment

class ReplyItemListViewModel(): ViewModel() {
    // textViewReplyNickName - text
    val textViewReplyNickNameText = MutableLiveData<String>()
    // textViewReplyText - text
    val textViewReplyTextText = MutableLiveData<String>()
    // textViewReplyTime - text
    val textViewReplyTimeText = MutableLiveData<String>()
}