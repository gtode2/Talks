package com.example.talks.database

import android.util.Log
import android.widget.Toast
import com.example.talks.data.PostData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject

class PostDatabase {
    companion object{
        fun getPosts(type:String="all",  onResult: (List<PostData>) -> Unit){
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
    }
}