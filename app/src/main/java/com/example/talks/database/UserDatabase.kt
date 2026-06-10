package com.example.talks.database

import android.util.Log
import com.example.talks.data.UserData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine

class UserDatabase {
    companion object{
        suspend fun getUser(uid:String):UserData = suspendCancellableCoroutine{cont->
            FirebaseFirestore.getInstance()
                .collection("Users")
                .document(uid)
                .get()
                .addOnSuccessListener { res ->
                    if (res.exists()) {
                        //0 = utente trovato

                        val fws:Long = res.get("followers") as Long
                        val fwd = res.get("followed") as Map<String, Boolean>
                        val user = UserData(
                            uid,
                            fws.toInt(),
                            fwd.size,
                            res.get("name").toString(),
                            res.get("surname").toString()
                        )
                        cont.resume(user){}
                    } else {
                        //-1 = utente non trovato
                        val user = UserData(uid, -1, 0)
                        cont.resume(user){}
                    }

                }
                .addOnFailureListener {
                    //-2 = errore
                    val user = UserData(uid, -2, 0)
                    cont.resume(user){}
                }
        }
        suspend fun follow(uid:String, user:String):Int = suspendCancellableCoroutine{cont->
            val db = FirebaseFirestore.getInstance()
            val userdb = db.collection("Users").document(uid)
            val profileUser = db.collection("Users").document(user)

            var ex=false

            db.runTransaction { tr->
                val prev = tr.get(userdb)
                    .get("followed") as? Map<String, Boolean>?: emptyMap()
                if (prev.containsKey(user)){
                    ex=true
                    throw Exception("already followed")
                }
                //aggiungo a map
                tr.update(userdb, "followed.$user", true)
                //incremento
                tr.update(profileUser, "followers", FieldValue.increment(1))
            }.addOnSuccessListener {
                cont.resume(0){}
            }.addOnFailureListener {e->
                if (ex){
                    //se esiste già
                    cont.resume(1){}
                }else{
                    //errore
                    Log.e("AAA", e.toString(), )
                    cont.resume(-1){}
                }
            }
            //se non seguito -> aggiungi -> return 0
            //se seguito -> return 1
            //errore -> return -1
        }
        suspend fun unfollow(uid:String, user:String):Int = suspendCancellableCoroutine{cont->
            val db = FirebaseFirestore.getInstance()
            val userdb = db.collection("Users").document(uid)
            val profileUser = db.collection("Users").document(user)

            var ex=true
            db.runTransaction { tr->
                val prev = tr.get(userdb)
                    .get("followed") as? Map<String,Boolean>?:emptyMap()
                if(!prev.containsKey(user)){
                    ex=false
                    throw Exception("not followed")
                }
                tr.update(userdb, "followed.$user", FieldValue.delete())
                tr.update(profileUser, "followers", FieldValue.increment(-1))
            }.addOnSuccessListener{
                cont.resume(0){}
            }.addOnFailureListener {
                if (!ex){
                    cont.resume(1){}
                }else{
                    cont.resume(-1){}
                }
            }
            //verifica utente seguito
            //se seguito -> elimina -> return 0
            //se non seguito -> return 1
            //errore -> return -1
        }
        suspend fun searchUser(string:String): UserData? = suspendCancellableCoroutine{cont->
            FirebaseFirestore.getInstance()
                .collection("Users")
                .document(string)
                .get()
                .addOnSuccessListener { res->
                    if (res.exists()){
                        val fwd = res.get("followed") as Map<String, Boolean>
                        val followers = (res.getLong("followers") ?: -1).toInt()
                        cont.resume(UserData(string, followers, fwd.size)){}
                    }else{
                        cont.resume(UserData("", -1, 0)){}
                    }
                }
                .addOnFailureListener { cont.resume(null){} }
        }

    }
}