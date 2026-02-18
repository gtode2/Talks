package com.example.talks.database

import com.google.firebase.firestore.FirebaseFirestore

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
                .addOnFailureListener {
                    onResult(-1)
                }
        }
    }
}