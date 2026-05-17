package com.example.talks.repository

import com.example.talks.database.LikeDatabase

object LikeRepository {
    private var likedPosts = mutableMapOf<String, Boolean>()

    suspend fun loadLikes(uid:String){
        //chiamata a LikeDatabase e poi memorizza
        val likesMap = LikeDatabase.userInit(uid)
        likedPosts = likesMap
    }

    fun getLikes():MutableMap<String, Boolean>{
        return likedPosts
    }

    fun addLike(uid:String, postid:String, onResult:(Int)->Unit){
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
    }
    fun isLiked(id:String): Boolean{
        return likedPosts.containsKey(id)
    }
    fun clear(){
        likedPosts.clear()
    }
}