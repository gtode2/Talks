package com.example.talks.database

import com.example.talks.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine

class AccountDatabase {
    companion object{
        suspend fun login(m:String, p:String):String = suspendCancellableCoroutine { cont ->
            val auth = FirebaseAuth.getInstance()
            auth.signInWithEmailAndPassword(m, p)
                .addOnSuccessListener { res ->
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
                            cont.resume(userTag) {}
                        }
                        .addOnFailureListener {

                        }
                }
                .addOnFailureListener {
                    //messaggio errore
                }
        }

        suspend fun register(m:String, p:String):Boolean = suspendCancellableCoroutine { cont->
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(m,p)
                .addOnSuccessListener { cont.resume(true){} }
                .addOnFailureListener { cont.resume(false){} }
        }

        suspend fun followers():String = suspendCancellableCoroutine { cont ->

        }

    }
}