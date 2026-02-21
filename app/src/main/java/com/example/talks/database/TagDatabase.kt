package com.example.talks.database

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class TagDatabase {
    companion object{
        fun checkUser(user: String, onResult:(Int)->Unit){
            FirebaseFirestore.getInstance()
                .collection("Users")
                .document(user)
                .get()
                .addOnSuccessListener {  res->
                    if (res.exists()){
                        onResult(0)
                    }else{
                        onResult(1)
                    }
                }
                .addOnFailureListener {e->
                    Log.e("AAA", e.toString())
                    onResult(-1)
                }
        }
        suspend fun addTag(tags:List<String>, post:String){
            //gestire differenza testo post e commenti

            var map = mutableMapOf<String, Boolean>()
            for (tag in tags) {
                var res = NotificationsDatabase.create(0, tag, post)

                map[tag]=res
                //false = errore
                //true = notifica aggiunta correttamente

            }
            //al termine -> map con risultati notifiche
            //capire se riprovare o annullare
        }

    }
}