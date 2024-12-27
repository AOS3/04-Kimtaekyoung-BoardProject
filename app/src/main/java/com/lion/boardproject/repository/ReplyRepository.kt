package com.lion.boardproject.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.lion.boardproject.util.ReplyState
import com.lion.boardproject.util.UserState
import com.lion.boardproject.vo.ReplyVO
import kotlinx.coroutines.tasks.await

class ReplyRepository {

    companion object {
        // 댓글 데이터를 저장하는 메서드
        suspend fun addReplyData(replyVO: ReplyVO): String {
            val firestore = FirebaseFirestore.getInstance()
            val collectionReference = firestore.collection("ReplyData")
            val documentReference = collectionReference.add(replyVO).await()
            return documentReference.id
        }

        // 댓글 목록을 가져오는 메서드
        suspend fun gettingBoardList(replyBoardId: String, replyState: ReplyState): MutableList<Map<String, *>> {
            val firestore = FirebaseFirestore.getInstance()
            val collectionReference = firestore.collection("ReplyData")
            val result = collectionReference.whereEqualTo("replyBoardId", replyBoardId)
                                            .whereEqualTo("replyState", replyState.number)
                                            .orderBy("replyTimeStamp", Query.Direction.DESCENDING).get().await()
            // 반환할 리스트
            val resultList = mutableListOf<Map<String, *>>()
            // 데이터의 수 만큼 반복한다.
            result.forEach {
                val map = mapOf(
                    "documentId" to it.id,
                    "replyVO" to it.toObject(ReplyVO::class.java)
                )
                resultList.add(map)
            }
            return resultList
        }

        // 댓글 상태를 변경하는 메서드
        suspend fun updateReplyState(replyDocumentId: String, newState: ReplyState) {
            val firestore = FirebaseFirestore.getInstance()
            val collectionReference = firestore.collection("ReplyData")
            val documentReference = collectionReference.document(replyDocumentId)
            val updateMap = mapOf(
                "replyState" to newState.number
            )

            documentReference.update(updateMap).await()
        }
    }
}