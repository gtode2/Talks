package com.example.talks.repository

import com.example.talks.database.PostDatabase

object BookmarkRepository {
    private var savedPosts = mutableMapOf<String, Boolean>()

    fun loadSaved(posts:Map<String, Boolean>){
        savedPosts = posts.toMutableMap()
    }

    fun getSaved():MutableMap<String,Boolean>{
        return savedPosts
    }

    fun savePost(uid:String, postid:String, onResult: (Int)->Unit){
        if (!savedPosts.containsKey(postid)){
            PostDatabase.savePost(uid, postid){ res->
                if (res==-1){
                    onResult(-1)
                }else if (res==0 || res==1){
                    savedPosts[postid] = true
                    onResult(0)
                }
            }
        }else{
            PostDatabase.unsavePost(uid, postid) { res ->
                if (res == -1) {
                    onResult(-1)
                } else if (res == 0 || res == 1) {
                    savedPosts.remove(postid)
                    onResult(1)
                }
            }
        }
    }

    fun isSaved(id: String): Boolean{
        return savedPosts.containsKey(id)
    }
    fun clear(){
        savedPosts.clear()
    }
}