package com.example.talks.database

import android.util.Log
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
            //0 -> tag in post
            //1 -> commento
            //2 -> follow

            val uid = UserID.getUID()
            if (uid.isNullOrBlank()){
                //gestione errore
                return false
            }


            //VERIFICARE COME VIENE PASSATO TAG, SE CON O SENZA @

            when(type) {
                0,1 -> {
                    val map = hashMapOf(
                        "type" to type,
                        "author" to uid, //utente che ha taggato / commentato
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
                        cont.resume(true) {}
                    }
                    .addOnFailureListener {
                        //gestione errore
                        cont.resume(false) {}
                    }
            }
        }

        suspend fun get(): List<NotificationData>{//cambiare tipo return
            return suspendCancellableCoroutine { cont ->
                var nl = mutableListOf<NotificationData>()

                val uid = UserID.getUID()
                if (uid.isNullOrBlank()){
                    //gestione errore
                    nl.add(NotificationData(true))
                    cont.resume(nl){}
                }

                //ottieni notifiche
                FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(uid!!)
                    .collection("notifications")
                    .orderBy("createdAt")
                    .get()
                    .addOnSuccessListener { res ->
                        for (n in res){
                            var notif = n.toObject(NotificationData::class.java)
                            nl.add(notif)
                        }
                        Log.e("AAA", nl.toString())
                        cont.resume(nl){}

                    }
                    .addOnFailureListener {
                        //gestione errore
                        nl.add(NotificationData(true))
                        cont.resume(nl){}
                    }

            }
        }
    }
}