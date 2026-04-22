package com.example.talks.database

import com.example.talks.singleton.UserID
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine

class ImageDatabase {
    companion object{
        suspend fun add(img:String, Id:String, profile:Boolean=false):Boolean = suspendCancellableCoroutine{ cont ->
            val uid = UserID.getUID()
            if (uid.isNullOrBlank()){
                //gestione errore
                cont.resume(false){}
            }

            var coll:String

            if (profile){
                coll = "ProfilePictures"
            }else{
                coll = "Images"
            }

            FirebaseFirestore.getInstance()
                .collection(coll)
                .document(Id)
                .set(hashMapOf("img" to img))
                .addOnSuccessListener {
                    cont.resume(true){}
                }
                .addOnFailureListener {
                    //gestire errore
                    cont.resume(false){}
                }
        }
        suspend fun get(postId:String):String = suspendCancellableCoroutine{cont->
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
