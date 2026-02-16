package com.example.talks.database

import com.example.talks.data.UserData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class UserDatabase {
    //ottenere numero followers
    //ottenere numero followed
    //eventualmente altre informazioni
    companion object{
        fun getUser(uid:String, onResult:(MutableMap<String, Int>)->Unit) {
            FirebaseFirestore.getInstance()
                .collection("Users")
                .document(uid)
                .get()
                .addOnSuccessListener { res ->

                    if (res.exists()) {
                        //0 = utente trovato
                        //devo restituire followers e followed
                        val map = mutableMapOf<String, Int>()
                        val fws:Long = res.get("followers") as Long
                        map["fw"] = fws.toInt()
                        val fwd = res.get("followed") as Map<String, Boolean>
                        map["fd"] = fwd.size
                        onResult(map)
                    } else {
                        //1 = utente non trovato
                        val map = mutableMapOf<String, Int>()
                        map["fw"] = -1
                        onResult(map)
                    }

                }
                .addOnFailureListener {
                    //-1 = errore
                    val map = mutableMapOf<String, Int>()
                    map["fw"] = -2
                    onResult(map)
                }
        }
        fun follow(uid:String, user:String, onResult:(Int)->Unit){
            val db = FirebaseFirestore.getInstance()
            val userdb = db.collection("Users").document(uid)
            val profileUser = db.collection("Users").document(user)

            var ex=false

            db.runTransaction { tr->
                //verifico presenza
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
                onResult(0)
            }.addOnFailureListener {
                if (ex){
                    //se esiste già
                    onResult(1)
                }else{
                    //errore
                    onResult(-1)
                }
            }
            //se non seguito -> aggiungi -> return 0
            //se seguito -> return 1
            //errore -> return -1
        }
        fun unfollow(uid:String, user:String, onResult: (Int)->Unit){
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
                onResult(0)
            }.addOnFailureListener {
                if (!ex){
                    onResult(1)
                }else{
                    onResult(-1)
                }
            }
            //verifica utente seguito
            //se seguito -> elimina -> return 0
            //se non seguito -> return 1
            //errore -> return -1
        }
        fun searchUser(string:String, onResult:(UserData)->Unit){
            FirebaseFirestore.getInstance()
                .collection("Users")
                .document(string)
                .get()
                .addOnSuccessListener { res->
                    if (res.exists()){
                        val fwd = res.get("followed") as Map<String, Boolean>
                        val followers = (res.getLong("followers") ?: -1).toInt()
                        onResult(UserData(string, followers, fwd.size))
                    }else{
                        onResult(UserData("", -1, 0))
                    }
                }
                .addOnFailureListener {  }
        }
    }
}