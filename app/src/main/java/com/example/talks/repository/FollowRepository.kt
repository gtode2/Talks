package com.example.talks.repository

import android.content.Context
import android.widget.Toast
import com.example.talks.R
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
    suspend fun addFollow(userid:String, context: Context):Int {
        //-1 -> errore
        //-2 -> uid non trovato -> manda a homepage
        //0 / 1 -> aggiunto (incrementa / non incrementare)
        //2 / 3 -> rimosso (decrementa / non decrementare)

        if (UserID.getUID() == null) {
            return -2
        }
        if (!followedAccounts.contains(userid)) {
            val res = UserDatabase.follow(UserID.getUID()!!, userid)
            if (res!=-1){

                followedAccounts[userid] = true
                when(res){
                    0-> {
                        val res = NotificationsDatabase.create(2, userid)
                        if (!res){
                            Toast.makeText(context, context.getString(R.string.errNotif), Toast.LENGTH_SHORT).show()
                        }
                        return 0
                    }
                    1-> return 1
                }
            }else{
                return -1
            }
        }else {

            val res = UserDatabase.unfollow(UserID.getUID()!!, userid)
            if (res != -1) {
                followedAccounts.remove(userid)
                when (res) {
                    0 -> return 2
                    1 -> return 3
                }
            } else {
                return -1
            }
        }
        return -1
    }
    fun clear(){
        followedAccounts.clear()
    }
}