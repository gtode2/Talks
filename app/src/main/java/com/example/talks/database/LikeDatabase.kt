package com.example.talks.database

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class LikeDatabase {
    companion object{
        fun getLikes(uid:String, onResult:(MutableMap<String, Boolean>) ->Unit){
            //richiede file likes utente
            FirebaseFirestore.getInstance()
                .collection("Users")
                .document(uid)
                .get()
                .addOnSuccessListener { res ->
                    val likedPosts = res.get("likes") as? Map<String, Boolean>?: emptyMap()
                    onResult(likedPosts.toMutableMap())
                }
        }
        fun addLike(uid:String, postid:String, onResult: (Int) -> Unit){
            val db = FirebaseFirestore.getInstance()
            val user = db.collection("Users").document(uid)
            val post = db.collection("Posts").document(postid)

            var ex = false //ex - exists in prev
            db.runTransaction{ tr ->
                //eseguo verifica presenza
                val prev= tr.get(user)
                    .get("likes") as? Map<String, Boolean>?: emptyMap()
                if (prev.containsKey(postid)){
                    ex=true
                    throw Exception("already liked")
                }
                //carico map
                tr.update(user, "likes.$postid",true)
                //incremento
                tr.update(post, "likes", FieldValue.increment(1))
            }
            .addOnSuccessListener {
                onResult(0)
            }
            .addOnFailureListener {
                if (ex){
                    onResult(1)
                }else{
                    onResult(-1)
                }
            }
        }
        fun removeLike(uid:String, postid:String, onResult: (Int) -> Unit){
            val db = FirebaseFirestore.getInstance()
            val user = db.collection("Users").document(uid)
            val post = db.collection("Posts").document(postid)

            var ex = true
            db.runTransaction{ tr->
                val prev = tr.get(user)
                    .get("likes") as? Map<String, Boolean>?: emptyMap()
                if (!prev.containsKey(postid)){
                    ex=false
                    throw Exception("not liked")
                }
                tr.update(user, "likes.$postid", FieldValue.delete())
                tr.update(post, "likes", FieldValue.increment(-1))
            }
            .addOnSuccessListener {
                onResult(0)
            }
            .addOnFailureListener {
                if (ex){
                    onResult(0)
                }else{
                    onResult(-1)
                }
            }
        }
    }
}