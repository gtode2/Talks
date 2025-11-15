package com.example.talks.database

import android.util.Log
import android.widget.Toast
import com.example.talks.data.PostData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject

class PostDatabase {
    companion object{
        fun getPosts(type:String="all", search: String="-1",  onResult: (List<PostData>) -> Unit){
            val pl = mutableListOf<PostData>()
            //all
            //followed accounts
            //your posts
            //saved
            //search
            if (type=="all"){
                FirebaseFirestore.getInstance()
                    .collection("Posts")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener { res ->
                        for (document in res){
                            var post = document.toObject(PostData::class.java)
                            post.id = document.id
                            pl.add(post)
                        }
                        onResult(pl)
                    }
            }
        }
        fun getPost(search: String,  onResult: (List<PostData>) -> Unit){
            val pl = mutableListOf<PostData>()
            FirebaseFirestore.getInstance()
                .collection("Posts")
                .document(search)
                .get()
                .addOnSuccessListener { document ->
                    var post = document.toObject(PostData::class.java)
                    if (post!=null) {
                        post.id = document.id
                        pl.add(post)
                    }
                    onResult(pl)
                }
        }

    }
}