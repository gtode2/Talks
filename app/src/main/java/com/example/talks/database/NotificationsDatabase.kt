package com.example.talks.database

import com.example.talks.data.NotificationData
import com.example.talks.singleton.UserID
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine

class NotificationsDatabase {
    companion object{
        suspend fun create(type:Int, p1:String, p2:String=""): Boolean{
            //crea map notifica in base al tipo

            //type
            //1 -> commento
            //2 -> follow

            val uid = UserID.getUID()
            if (uid.isNullOrBlank()){
                return false
            }else if (uid == p1){
                //blocca notifica a se stesso
                return true
            }

            when(type) {
                1 -> {
                    val map = hashMapOf(
                        "type" to type,
                        "author" to uid, //utente che ha commentato
                        "src" to p2, //id del post
                        "createdAt" to FieldValue.serverTimestamp(),
                    )

                    val res = add(map, p1)
                    return res
                }

                2 -> {
                    val map = hashMapOf(
                        "type" to type,
                        "author" to uid, //utente che ha seguito
                        "src" to uid, //uguale a author
                        "createdAt" to FieldValue.serverTimestamp(),
                    )

                    val res = add(map, p1)
                    return res
                }

                else -> return false //type errato
            }
        }

        private suspend fun add(map: Map<*,*>,user:String):Boolean {
            //pubblica notifica
            return suspendCancellableCoroutine { cont ->
                val db = FirebaseFirestore.getInstance()
                db.collection("Users")
                    .document(user)
                    .collection("notifications")
                    .add(map)
                    .addOnSuccessListener {
                        cont.resume(true, {_,_,_->})
                    }
                    .addOnFailureListener {
                        cont.resume(false, {_,_,_->})
                    }
            }
        }

        suspend fun get(): List<NotificationData>{
            return suspendCancellableCoroutine { cont ->
                val nl = mutableListOf<NotificationData>()

                val uid = UserID.getUID()
                if (uid.isNullOrBlank()){
                    nl.add(NotificationData(true, "nl")) //not logged
                    cont.resume(nl, {_,_,_->})
                }

                FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(uid!!)
                    .collection("notifications")
                    .orderBy("createdAt")
                    .get()
                    .addOnSuccessListener { res ->
                        for (n in res){
                            val notif = n.toObject(NotificationData::class.java)
                            nl.add(notif)
                        }
                        cont.resume(nl, {_,_,_->})
                    }
                    .addOnFailureListener {
                        nl.add(NotificationData(true))
                        cont.resume(nl, {_,_,_->})
                    }

            }
        }
    }
}