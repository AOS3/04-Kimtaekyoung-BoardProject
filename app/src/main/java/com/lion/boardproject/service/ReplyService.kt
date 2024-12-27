package com.lion.boardproject.service

import com.lion.boardproject.model.ReplyModel
import com.lion.boardproject.repository.ReplyRepository
import com.lion.boardproject.repository.UserRepository
import com.lion.boardproject.util.ReplyState
import com.lion.boardproject.util.UserState
import com.lion.boardproject.vo.ReplyVO

class ReplyService {

    companion object {
        // 댓글 데이터를 저장하는 메서드
        suspend fun addReplyData(replyModel: ReplyModel): String {
            val id = ReplyRepository.addReplyData(replyModel.toReplyVO())
            return id
        }

        // 댓글 목록을 가져오는 메서드
        suspend fun gettingReplyList(replyBoardId: String, replyState: ReplyState): MutableList<ReplyModel> {
            // 댓글 목록을 가져온다.
            val replyList = mutableListOf<ReplyModel>()
            val resultList = ReplyRepository.gettingBoardList(replyBoardId, replyState)
            resultList.forEach {
                val replyVO = it["replyVO"] as ReplyVO
                val documentId = it["documentId"] as String
                val replyModel = replyVO.toReplyModel(documentId)

                replyList.add(replyModel)
            }
            return replyList
        }

        // 댓글의 상태를 변경하는 메서드
        suspend fun updateReplyState(replyDocumentId:String, newState: ReplyState){
            ReplyRepository.updateReplyState(replyDocumentId, newState)
        }
    }
}