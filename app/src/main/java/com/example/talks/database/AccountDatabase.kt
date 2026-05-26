package com.example.talks.database

import com.example.talks.data.UserData
import com.example.talks.singleton.UserID
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
                            if (userTag==""){
                                //account esiste ma non registrati
                                //imposto temporaneamente UserID a id firebase
                                UserID.setUID(uid)
                            }
                            //FollowRepository
                            cont.resume(userTag) {}
                        }
                        .addOnFailureListener {

                        }
                }
                .addOnFailureListener {
                    //messaggio errore
                }
        }

        suspend fun register(m:String, p:String):String = suspendCancellableCoroutine { cont->
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(m,p)
                .addOnSuccessListener {
                    val uid = FirebaseAuth.getInstance().currentUser!!.uid
                    cont.resume(uid){} }
                .addOnFailureListener { cont.resume(""){} }
        }

        suspend fun createAccount(name:String, surname:String, username:String, dob:String, uid:String):Boolean = suspendCancellableCoroutine{ cont->
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

            FirebaseFirestore.getInstance()
                .collection("Users")
                .document(username)
                .set(user)
                .addOnSuccessListener { cont.resume(true){} }
                .addOnFailureListener { cont.resume(false){} }

        }
    }
}