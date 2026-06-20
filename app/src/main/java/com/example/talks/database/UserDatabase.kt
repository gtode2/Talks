package com.example.talks.database

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
                        //utente trovato

                        val fws:Long = res.get("followers") as Long
                        val fwd = res.get("followed") as Map<String, Boolean>
                        val user = UserData(
                            uid,
                            fws.toInt(),
                            fwd.size,
                            res.get("name").toString(),
                            res.get("surname").toString()
                        )
                        cont.resume(user, {_,_,_->})
                    } else {
                        //utente non trovato
                        val user = UserData(uid, err="n")//not found
                        cont.resume(user, {_,_,_->})
                    }
                }
                .addOnFailureListener {
                    //errore generico
                    val user = UserData(uid, err="")
                    cont.resume(user, {_,_,_->})
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
                cont.resume(0, {_,_,_->}) //aggiunto
            }.addOnFailureListener {
                if (ex){
                    cont.resume(1){} //già seguito
                }else{
                    cont.resume(-1){} //errore generico
                }
            }
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
                cont.resume(0, {_,_,_->}) //follow rimosso
            }.addOnFailureListener {
                if (!ex){
                    cont.resume(1,{_,_,_->}) //non seguito
                }else{
                    cont.resume(-1,{_,_,_->})//errore generico
                }
            }
        }
        suspend fun searchUser(string:String): UserData = suspendCancellableCoroutine{cont->
            FirebaseFirestore.getInstance()
                .collection("Users")
                .document(string)
                .get()
                .addOnSuccessListener { res->
                    if (res.exists()){
                        val fwd = res.get("followed") as Map<String, Boolean>
                        val followers = (res.getLong("followers") ?: -1).toInt()
                        cont.resume(UserData(string, followers, fwd.size),{_,_,_->})
                    }else{
                        cont.resume(UserData("", err="n"),{_,_,_->})
                    }
                }
                .addOnFailureListener { cont.resume(UserData(string, err=""),{_,_,_->})}
        }

    }
}