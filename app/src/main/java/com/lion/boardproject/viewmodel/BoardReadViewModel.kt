package com.lion.boardproject.viewmodel

import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.lion.boardproject.fragment.BoardReadFragment
import com.lion.boardproject.model.ReplyModel

class BoardReadViewModel(val boardReadFragment: BoardReadFragment): ViewModel() {
    // textFieldBoardReadTitle - text
    val textFieldBoardReadTitleText = MutableLiveData(" ")
    // textFieldBoardReadNickName - text
    val textFieldBoardReadNickName = MutableLiveData(" ")
    // textFieldBoardReadType - text
    val textFieldBoardReadTypeText = MutableLiveData(" ")
    // textFieldBoardReadText - text
    val textFieldBoardReadTextText = MutableLiveData(" ")
    // editTextBoardReadReply - text
    val editTextBoardReadReplyText = MutableLiveData<String>()
    // textViewReply - text
    val textViewReplyText = MutableLiveData<String>()

    // button - save
    fun saveButton() {
        boardReadFragment.saveButtonMethod()
    }

    companion object{
        // toolbarBoardRead - onNavigationClickBoardRead
        @JvmStatic
        @BindingAdapter("onNavigationClickBoardRead")
        fun onNavigationClickBoardRead(materialToolbar: MaterialToolbar, boardReadFragment: BoardReadFragment){
            materialToolbar.setNavigationOnClickListener {
                boardReadFragment.movePrevFragment()
            }
        }
    }
}