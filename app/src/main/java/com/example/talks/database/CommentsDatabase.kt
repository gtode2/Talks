package com.example.talks.database

import com.example.talks.data.CommentData
import com.example.talks.singleton.UserID
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
                    cont.resume(comm, {_,_,_->})
                }.addOnFailureListener {
                    //gestire oflisnr
                }
        }
        suspend fun addComment(text:String, post:String, postOwner:String):Int = suspendCancellableCoroutine{cont->
            val uid = UserID.getUID()
            val comment = hashMapOf(
                "date" to FieldValue.serverTimestamp(),
                "text" to text,
                "uid" to uid
            )
            FirebaseFirestore.getInstance()
                .collection("Posts")
                .document(post)
                .collection("comments")
                .add(comment)
                .addOnSuccessListener{
                    CoroutineScope(Dispatchers.Default).launch{
                        val res = NotificationsDatabase.create(1, postOwner,post)
                        if (!res){
                            //errore
                        }
                    }
                    cont.resume(0, {_,_,_->})
                }.addOnFailureListener {
                    cont.resume(-1, {_,_,_->})
                }
        }
        suspend fun count(post:String):Int = suspendCancellableCoroutine { cont->
            FirebaseFirestore.getInstance()
                .collection("Posts")
                .document(post)
                .collection("comments")
                .count()
                .get(AggregateSource.SERVER)
                .addOnSuccessListener { res->
                    cont.resume(res.count.toInt(),{_,_,_->})
                }.addOnFailureListener { cont.resume(0, {_,_,_->})}
        }
    }
}