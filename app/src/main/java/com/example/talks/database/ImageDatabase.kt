package com.example.talks.database

import android.graphics.Bitmap
import com.example.talks.data.ImageData
import com.example.talks.data.ImageDataRes
import com.example.talks.managers.ImageManager
import com.example.talks.singleton.UserID
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine

class ImageDatabase {
    companion object{
        suspend fun add(img:String, Id:String, profile:Boolean=false):Boolean = suspendCancellableCoroutine{ cont ->
            val uid = UserID.getUID()
            if (uid.isNullOrBlank()){
                cont.resume(false, {_,_,_->})
            }

            var coll:String = if(profile) "ProfilePictures" else "Images"


            FirebaseFirestore.getInstance()
                .collection(coll)
                .document(Id)
                .set(hashMapOf("img" to img))
                .addOnSuccessListener {
                    cont.resume(true, {_,_,_->})
                }
                .addOnFailureListener {
                    cont.resume(false, {_,_,_->})
                }
        }
        suspend fun get(id:String, profile:Boolean=false): ImageDataRes? = suspendCancellableCoroutine{ cont->
            val coll:String = if(profile) "ProfilePictures" else "Images"
            FirebaseFirestore.getInstance()
                .collection(coll)
                .document(id)
                .get()
                .addOnSuccessListener { res ->
                    if (res.get("img")==null){
                        //non trova immagine
                        cont.resume(ImageDataRes(null), { _, _, _->})
                    }else{
                        //trova immagine
                        val bmp = ImageManager.decode(res.get("img") as String)
                        cont.resume(ImageDataRes(bmp), {_,_,_->})
                    }
                }
                .addOnFailureListener {
                    cont.resume(null,{_,_,_->})
                }
            }
        suspend fun remove(profile:Boolean=false, postid:String=""):Boolean = suspendCancellableCoroutine { cont->
            var id = ""
            if (profile){
                if (UserID.getUID()==null){
                    //gestire errore
                }else{
                    id = UserID.getUID()!!
                }
            }else{
                id = postid
            }
            val coll = if (profile) "ProfilePictures" else "Images"

            FirebaseFirestore.getInstance()
                .collection(coll)
                .document(id)
                .delete()
                .addOnSuccessListener {
                    cont.resume(true, {_,_,_->})
                }
                .addOnFailureListener {
                    cont.resume(false, {_,_,_->})
                }
        }
    }
}
