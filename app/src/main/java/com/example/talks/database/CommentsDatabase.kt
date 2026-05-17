package com.example.talks.database

import com.example.talks.data.CommentData
import com.example.talks.interfaces.Comment
import com.example.talks.singleton.UserID
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
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
                    cont.resume(comm){}
                }
        }
        suspend fun addComment(text:String, post:String, postOwner:String):Int = suspendCancellableCoroutine{cont->
            val uid = UserID.getUID()
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
                    CoroutineScope(Dispatchers.IO).launch{
                        NotificationsDatabase.create(1, postOwner,post)
                    }


                    cont.resume(0){}
                }.addOnFailureListener {
                    cont.resume(-1){}
                }
        }
    }
}