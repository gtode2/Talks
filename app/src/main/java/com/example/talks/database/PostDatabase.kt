package com.example.talks.database

import android.util.Log
import android.widget.Toast
import androidx.compose.ui.text.rememberTextMeasurer
import com.example.talks.data.PostData
import com.example.talks.repository.BookmarkRepository
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject

class PostDatabase {
    companion object{
        fun getPosts(type:String="all", search: String="-1",  onResult: (List<PostData>) -> Unit) {
            val pl = mutableListOf<PostData>()
            //all
            //followed accounts
            //your posts
            //search
            when (type) {
                "all" -> {
                    FirebaseFirestore.getInstance()
                        .collection("Posts")
                        .orderBy("createdAt", Query.Direction.DESCENDING)
                        .get()
                        .addOnSuccessListener { res ->
                            for (document in res) {
                                var post = document.toObject(PostData::class.java)
                                post.id = document.id
                                pl.add(post)
                            }
                            onResult(pl)
                        }
                }
                "your"->{
                    FirebaseFirestore.getInstance()
                        .collection("Posts")
                        .orderBy("createdAt", Query.Direction.DESCENDING)
                        //UID lo passo come parametro "search"
                        .whereEqualTo("users.uid",search)
                        .get()
                        .addOnSuccessListener { res ->
                            for (document in res) {
                                var post = document.toObject(PostData::class.java)
                                post.id = document.id
                                pl.add(post)
                            }
                            onResult(pl)
                        }
                }
                "saved"->{
                    getSaved(search){res->
                        FirebaseFirestore.getInstance()
                            .collection("Posts")
                            .whereIn(FieldPath.documentId(), res)
                            .get()
                            .addOnSuccessListener { result->
                                for (document in result){
                                    var post = document.toObject(PostData::class.java)
                                    post.id = document.id
                                    pl.add(post)
                                }
                                onResult(pl)
                            }.addOnFailureListener {

                            }
                    }
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
        private fun getSaved(uid:String, onResult: (List<String>) -> Unit){
            FirebaseFirestore.getInstance()
                .collection("Users")
                .document(uid)
                .get()
                .addOnSuccessListener { res ->
                    val savedPosts = res.get("saved") as? Map<String, Boolean>?: emptyMap()
                    onResult(savedPosts.keys.toList())
                }
        }
        fun savePost(uid:String, postid:String, onResult: (Int) -> Unit){
            val db = FirebaseFirestore.getInstance()
            val user = db.collection("Users").document(uid)

            var ex=false
            db.runTransaction{ tr->
                val prev = tr.get(user)
                    .get("saved") as?Map<String,Boolean>?: emptyMap()
                if (prev.containsKey(postid)){
                    ex=true
                    throw Exception("already saved")
                }
                tr.update(user, "saved.$postid", true)
            }.addOnSuccessListener {
                onResult(0)
            }.addOnFailureListener {
                if (ex){
                    onResult(1)
                }else{
                    onResult(-1)
                }
            }
        }
        fun unsavePost(uid:String, postid:String, onResult: (Int) -> Unit){
            val db = FirebaseFirestore.getInstance()
            val user = db.collection("Users").document(uid)

            var ex=true
            db.runTransaction{tr->
                val prev = tr.get(user)
                    .get("saved") as?Map<String,Boolean>?: emptyMap()
                if (!prev.containsKey(postid)){
                    ex=false
                    throw Exception("not found")
                }
                tr.update(user,"saved.$postid",FieldValue.delete())
            }.addOnSuccessListener {
                onResult(0)
            }.addOnFailureListener {
                if (!ex){
                    onResult(1)
                }else{
                    onResult(-1)
                }
            }
        }
    }
}