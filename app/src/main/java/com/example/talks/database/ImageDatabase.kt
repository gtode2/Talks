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

            var coll:String = if(profile) "ProfilePictures" else "Images"


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
        suspend fun get(id:String, profile:Boolean=false):String? = suspendCancellableCoroutine{cont->
            var coll:String = if(profile) "ProfilePictures" else "Images"

            FirebaseFirestore.getInstance()
                .collection(coll)
                .document(id)
                .get()
                .addOnSuccessListener { res ->
                    if (res.get("img")==null){
                        cont.resume(null){}
                    }else{
                        cont.resume(res.get("img") as String) {}
                    }
                }
                .addOnFailureListener {
                    //gestire errore
                    cont.resume("") {}
                }
            }
    }
}
