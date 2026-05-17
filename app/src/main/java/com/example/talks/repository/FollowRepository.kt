package com.example.talks.repository

import com.example.talks.database.NotificationsDatabase
import com.example.talks.database.UserDatabase
import com.example.talks.singleton.UserID

object FollowRepository {
    private var followedAccounts = mutableMapOf<String, Boolean>()

    fun loadFollowed(users:Map<String, Boolean>){
        followedAccounts = users.toMutableMap()
    }

    fun isFollowed(id:String): Boolean{
        return followedAccounts.containsKey(id)
    }
    suspend fun addFollow(userid:String):Int {
        //-1 -> errore
        //-2 -> uid non trovato -> manda a homepage
        //0 / 1 -> aggiunto (incrementa / non incrementare)
        //2 / 3 -> rimosso (decrementa / non decrementare)

        if (UserID.getUID() == null) {
            return -2
        }
        if (!followedAccounts.contains(userid)) {
            //aggiungi follow

            val res = UserDatabase.follow(UserID.getUID()!!, userid)
            if (res!=-1){
                followedAccounts.put(userid, true)
                when(res){
                    0-> {
                        NotificationsDatabase.create(2, userid)
                        return 0
                    }
                    1-> return 1
                }
            }else{
                return -1
            }
        }else {
            //rimuovi follow

            val res = UserDatabase.unfollow(UserID.getUID()!!, userid)
            if (res != -1) {
                followedAccounts.remove(userid)
                when (res) {
                    0 -> return 3
                    1 -> return 4
                }
            } else {
                return -1
            }
        }
        return -1 //irraggiungibile | solo per evitare errore
    }
    fun clear(){
        followedAccounts.clear()
    }

}