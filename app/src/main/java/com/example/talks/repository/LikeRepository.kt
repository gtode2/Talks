package com.example.talks.repository

import android.widget.Toast
import com.example.talks.database.LikeDatabase

object LikeRepository {
    private var likedPosts = mutableMapOf<String, Boolean>()

    fun loadLikes(likes:Map<String,Boolean>){
        likedPosts = likes.toMutableMap()
    }

    fun getLikes():MutableMap<String, Boolean>{
        return likedPosts
    }

    fun addLike(uid:String, postid:String, onResult:(Int)->Unit){
        if (!likedPosts.contains(postid)){
            LikeDatabase.addLike(uid, postid){res->
                if (res==-1){
                    onResult(-1)
                }else if (res==0 || res==1){
                    likedPosts.put(postid, true)
                    onResult(0)
                }
            }
        }else{
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