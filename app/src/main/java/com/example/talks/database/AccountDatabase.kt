package com.example.talks.database

import com.example.talks.singleton.UserID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine

class AccountDatabase {
    companion object{
        suspend fun login(m:String, p:String):String? = suspendCancellableCoroutine { cont ->
            val auth = FirebaseAuth.getInstance()
            auth.signInWithEmailAndPassword(m, p)
                .addOnSuccessListener {
                    val user = auth.currentUser
                    val uid = user!!.uid
                    var userTag = ""

                    FirebaseFirestore.getInstance()
                        .collection("Users")
                        .whereEqualTo("authid", uid)
                        .get()
                        .addOnSuccessListener {
                            for (doc in it) {
                                userTag = doc.id
                            }
                            if (userTag==""){
                                //account esiste ma non registrato
                                //imposto TempID a id firebase
                                UserID.setTemp(uid)
                            }
                            cont.resume(userTag,{_,_,_->})
                        }
                        .addOnFailureListener {
                            //errpre ottenimento info utente
                            cont.resume(null, {_,_,_->})
                        }
                }
                .addOnFailureListener {
                    cont.resume(null, {_,_,_->})
                }
        }

        suspend fun register(m:String, p:String):String? = suspendCancellableCoroutine { cont->
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(m,p)
                .addOnSuccessListener {
                    val uid = FirebaseAuth.getInstance().currentUser!!.uid
                    cont.resume(uid, {_,_,_->})}
                .addOnFailureListener {e->
                    if (e is FirebaseAuthUserCollisionException){
                        cont.resume("", {_,_,_->})
                    }else{
                        cont.resume(null, {_,_,_->})
                    }


                }
        }

        suspend fun createAccount(name:String, surname:String, username:String, dob:String, uid:String):Int = suspendCancellableCoroutine{ cont->
            val db = FirebaseFirestore.getInstance()
            var ex = false
            val user = hashMapOf(
                "authid" to uid,
                "name" to name,
                "surname" to surname,
                "bday" to dob,
                "followers" to 0,
                "likes" to mutableMapOf<String,Boolean>(),
                "saved" to mutableMapOf<String, Boolean>(),
                "followed" to mutableMapOf<String, Boolean>()
            )

            db.runTransaction { tr->
                val prevuser = tr.get(db.collection("Users").document(username))
                if (prevuser.exists()){
                    ex=true
                    throw Exception("Duplicated")

                }else{
                    tr.set(db.collection("Users").document(username), user)
                    cont.resume(0, {_,_,_->})
                }
            }.addOnSuccessListener {
                //gestire oslisnr
            }.addOnFailureListener {
                if (ex){
                    cont.resume(-2, {_,_,_->})
                }else{
                    cont.resume(-1, {_,_,_->})
                }
            }
        }
    }
}