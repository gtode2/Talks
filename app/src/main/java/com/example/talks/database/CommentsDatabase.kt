package com.example.talks.database

import com.example.talks.data.CommentData
import com.example.talks.interfaces.Comment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.suspendCancellableCoroutine

class CommentsDatabase {
    companion object{
        suspend fun getComments(post:String):MutableList<CommentData> = suspendCancellableCoroutine{cont->
            val comm = mutableListOf<CommentData>()
            FirebaseFirestore.getInstance()
                .collection("Posts")
                .document(post)
                .collection("comments")
                .orderBy("date",Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { res ->
                    for(document in res){
                        var comment = document.toObject(CommentData::class.java)
                        comm.add(comment)
                    }
                    cont.resume(comm){}
                }
        }
        fun addComment(uid:String, text:String, post:String, onResult: (Int) -> Unit){
            val comment = hashMapOf(
                "date" to FieldValue.serverTimestamp(),
                //"postid" to post, capire se serve
                "text" to text,
                "uid" to uid
            )
            FirebaseFirestore.getInstance()
                .collection("Posts")
                .document(post)
                .collection("comments")
                .add(comment)
                .addOnSuccessListener{
                    //aggiungi notifica -> type = 1
                    onResult(0)
                }.addOnFailureListener {
                    onResult(-1)
                }
        }
    }
}