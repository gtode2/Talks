package com.example.talks.database

import android.util.Log
import android.widget.Toast
import com.example.talks.data.PostData
import com.example.talks.singleton.UserID
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.suspendCancellableCoroutine


class PostDatabase {
    companion object{
        suspend fun getPosts(type:String="all", search: String?=null):List<PostData>? = suspendCancellableCoroutine{cont->
            val pl = mutableListOf<PostData>()

            when (type) {
                "all" -> {
                    FirebaseFirestore.getInstance()
                        .collection("Posts")
                        .orderBy("createdAt", Query.Direction.DESCENDING)
                        .get()
                        .addOnSuccessListener { res ->
                            for (document in res) {
                                val post = document.toObject(PostData::class.java)
                                if (post.uid!=UserID.getUID()){
                                    post.id = document.id
                                    pl.add(post)
                                }
                            }
                            cont.resume(pl,{_,_,_->})
                        }.addOnFailureListener {
                            cont.resume(null, {_,_,_->})
                        }
                }
                "user"->{
                    FirebaseFirestore.getInstance()
                        .collection("Posts")
                        //UID lo passo come parametro "search"
                        .whereEqualTo("uid",search!!)
                        .orderBy("createdAt", Query.Direction.DESCENDING)
                        .get()
                        .addOnSuccessListener { res ->
                            for (document in res) {
                                val post = document.toObject(PostData::class.java)
                                post.id = document.id
                                pl.add(post)
                            }
                            cont.resume(pl, {_,_,_->})
                        }.addOnFailureListener {
                            cont.resume(null, {_,_,_->})
                        }
                }
                "saved"->{
                    FirebaseFirestore.getInstance()
                        .collection("Users")
                        .document(search!!)
                        .get()
                        .addOnSuccessListener { res ->
                            val savedPosts = res.get("saved") as? Map<String, Boolean>?: emptyMap()
                            val list = savedPosts.keys.toList()

                            if (list.isNotEmpty()){
                                FirebaseFirestore.getInstance()
                                    .collection("Posts")
                                    .whereIn(FieldPath.documentId(), list)
                                    .get()
                                    .addOnSuccessListener { result->
                                        for (document in result){
                                            val post = document.toObject(PostData::class.java)
                                            post.id = document.id
                                            pl.add(post)
                                        }
                                        cont.resume(pl, {_,_,_->})
                                    }.addOnFailureListener {
                                        cont.resume(null, {_,_,_->})
                                    }
                            }else{
                                val el = emptyList<PostData>()
                                cont.resume(el, {_,_,_->})
                            }
                        }.addOnFailureListener {
                            cont.resume(null, {_,_,_->})
                        }
                }
                "search"->{
                    FirebaseFirestore.getInstance()
                        .collection("Posts")
                        .where(
                            Filter.or(
                                Filter.equalTo("post", search),
                                Filter.equalTo("source", search),
                                Filter.equalTo("title",search),
                                Filter.equalTo("uid",search)
                            )
                        )
                        .orderBy("createdAt", Query.Direction.DESCENDING)
                        .get()
                        .addOnSuccessListener {res->
                            for (document in res){
                                val post=document.toObject(PostData::class.java)
                                if (post.uid!=UserID.getUID()){
                                    post.id = document.id
                                    pl.add(post)
                                }
                            }
                            cont.resume(pl, {_,_,_->})
                        }
                        .addOnFailureListener {
                            cont.resume(null, {_,_,_->})
                        }
                }
            }
        }
        suspend fun getPost(search: String):List<PostData>? = suspendCancellableCoroutine { cont->
            val pl = mutableListOf<PostData>()
            FirebaseFirestore.getInstance()
                .collection("Posts")
                .document(search)
                .get()
                .addOnSuccessListener { document ->
                    val post = document.toObject(PostData::class.java)
                    if (post!=null) {
                        post.id = document.id
                        pl.add(post)
                    }
                    cont.resume(pl, {_,_,_->})
                }
                .addOnFailureListener {
                    cont.resume(null, {_,_,_->})
                }
        }
        /*private fun getSaved(uid:String, onResult: (List<String>) -> Unit){
            FirebaseFirestore.getInstance()
                .collection("Users")
                .document(uid)
                .get()
                .addOnSuccessListener { res ->
                    val savedPosts = res.get("saved") as? Map<String, Boolean>?: emptyMap()
                    onResult(savedPosts.keys.toList())
                }
        }*/
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


        suspend fun createPost(uid:String, post:String, source: String, title:String, img:Boolean):String = suspendCancellableCoroutine{ cont->
            val db = FirebaseFirestore.getInstance()
            val postContent = hashMapOf(
                "uid" to uid,
                "likes" to 0,
                "post" to post,
                "source" to source,
                "title" to title,
                "image" to img,
                "createdAt" to FieldValue.serverTimestamp()
            )
            db.collection("Posts")
            .add(postContent)
            .addOnSuccessListener { ref->
                val id = ref.id
                cont.resume(id, {_,_,_->})
            }
            .addOnFailureListener {
                cont.resume("-1",{_,_,_->})
            }
        }

        fun deletePost(uid:String, postid: String, onResult: (Int) -> Unit){
            val db = FirebaseFirestore.getInstance()
            val post = db.collection("Posts").document(postid)

            var ex=true
            db.runTransaction{tr->
                var p = tr.get(post)
                if (!p.exists()){
                    ex=false
                    throw Exception("not found")
                }

                var prev:String? = p.get("uid").toString()
                if (prev!=uid){
                    throw Exception("Unauthorized")
                }
                post.delete()
            }.addOnSuccessListener {
                onResult(0)
            }.addOnFailureListener {
                if (!ex){
                    onResult(1)
                }else{
                    onResult(-1)
                }
            }
            //verifica esistenza post
            //elimina post
            //0 -> eseguito correttamente
            //-1-> errore
        }
        suspend fun editPost(uid:String, postid: String,data:PostData):Int = suspendCancellableCoroutine{cont->
            val db = FirebaseFirestore.getInstance()
            val post = db.collection("Posts").document(postid)

            var ex=true
            db.runTransaction { tr->
                //verifico possesso post
                var p = tr.get(post)
                if (!p.exists()){
                    ex=false
                    throw Exception("not found")
                }

                val prev: String = p.get("uid").toString()
                if (prev!=uid){
                    throw Exception("Unauthorized")
                }



                if(data.title!=""){
                    tr.update(post,"title", data.title)
                }
                if(data.post!=""){
                    tr.update(post,"post", data.post)
                }
                if(data.source!=""){
                    tr.update(post,"source", data.source)
                }

            }.addOnSuccessListener {
                cont.resume(0, {_,_,_->})
            }.addOnFailureListener {
                if (ex){
                    cont.resume(-1, {_,_,_->})
                }else{
                    cont.resume(1, {_,_,_->})
                }
            }


        }
        suspend fun editImgPost(id:String, img: Boolean):Boolean = suspendCancellableCoroutine{cont->
            FirebaseFirestore.getInstance()
                .collection("Posts")
                .document(id)
                .update("image", img)
                .addOnSuccessListener { cont.resume(true, {_,_,_->})}
                .addOnFailureListener { cont.resume(false,{_,_,_->})}
        }
    }
}