package com.example.talks.repository

import com.example.talks.database.UserDatabase
import com.example.talks.singleton.UserID

object FollowRepository {
    private var followedAccounts = mutableMapOf<String, Boolean>()

    fun loadFollowed(users:Map<String, Boolean>){
        followedAccounts = users.toMutableMap()
    }

    fun getFollowed():MutableMap<String, Boolean>{
        return followedAccounts
    }

    fun getFollowed(uid:String, postid:String, onResult:(Int)->Unit){
        /*
        if (!likedPosts.contains(postid)){
            LikeDatabase.addLike(uid, postid){res->
                if (res==-1){
                    //errore nell'inserimento del like
                    onResult(-1)
                }else if (res==0 || res==1){
                    likedPosts.put(postid, true)
                    onResult(0)
                }
            }
        }else{
            //già presente - rimozione
            LikeDatabase.removeLike(uid, postid){res->
                if (res==0 || res==1){
                    likedPosts.remove(postid)
                    onResult(1)
                }else{
                    onResult(-1)
                }
            }

        }
        */
    }
    fun isFollowed(id:String): Boolean{
        return followedAccounts.containsKey(id)
    }
    suspend fun addFollow(userid:String):Int {
        //-1 -> errore
        //-2 -> uid non trovato -> manda a homepage
        //0 -> aggiunto
        //1 -> rimosso

        if (UserID.getUID() == null) {
            return -2
        }
        if (!followedAccounts.contains(userid)) {
            //aggiungi follow

            val res = UserDatabase.follow(UserID.getUID()!!, userid)
            if (res!=-1){
                followedAccounts.put(userid, true)
                return 0
            }else{
                return -1
                TODO("gestione errore")
            }
        }else {
            //rimuovi follow

            val res = UserDatabase.unfollow(UserID.getUID()!!, userid)
            if (res != -1) {
                followedAccounts.remove(userid)
                return 1
            } else {
                return -1
                TODO("gestione errore")
            }
        }
    }
    fun clear(){
        followedAccounts.clear()
    }

}