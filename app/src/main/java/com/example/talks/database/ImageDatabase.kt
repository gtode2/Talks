package com.example.talks.database

import com.example.talks.singleton.UserID
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine

class ImageDatabase {
    companion object{
        suspend fun add(img:String, postId:String):Boolean{
            val uid = UserID.getUID()
            if (uid.isNullOrBlank()){
                //gestione errore
                return false
            }
            return suspendCancellableCoroutine { cont ->
                FirebaseFirestore.getInstance()
                    .collection("Images")
                    .document(postId)
                    .set(hashMapOf("img" to img))
                    .addOnSuccessListener {
                        cont.resume(true){}
                    }
                    .addOnFailureListener {
                        //gestire errore
                        cont.resume(false){}
                    }

            }
        }
        suspend fun get(postId:String):String {
            return suspendCancellableCoroutine { cont ->
                FirebaseFirestore.getInstance()
                    .collection("Images")
                    .document(postId)
                    .get()
                    .addOnSuccessListener { res ->
                        cont.resume(res.get("img") as String) {}
                    }
                    .addOnFailureListener {
                        //gestire errore
                        cont.resume("") {}
                    }
            }
        }
    }
}