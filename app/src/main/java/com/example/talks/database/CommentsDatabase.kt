package com.example.talks.database

import com.example.talks.data.CommentData
import com.example.talks.interfaces.Comment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class CommentsDatabase {
    companion object{
        fun getComments(post:String, onResult:(List<CommentData>) -> Unit){
            val comm = mutableListOf<CommentData>()
            FirebaseFirestore.getInstance()
                .collection("Posts")
                .document(post)
                .collection("comments")
                .get()
                .addOnSuccessListener { res ->
                    for(document in res){
                        var comment = document.toObject(CommentData::class.java)
                        comm.add(comment)
                    }
                    onResult(comm)
                }
        }
    }
}