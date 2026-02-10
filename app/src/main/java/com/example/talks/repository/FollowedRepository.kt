package com.example.talks.repository

import com.example.talks.database.LikeDatabase

object FollowedRepository {
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
    fun addFollowed(userid:String){
        followedAccounts.put(userid, true)
    }
    fun removeFollowed(userid:String){
        followedAccounts.remove(userid)
    }
}