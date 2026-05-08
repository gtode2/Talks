package com.example.talks.repository

import com.example.talks.database.LikeDatabase
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
            //add
            PostDatabase.savePost(uid, postid){ res->
                if (res==-1){
                    onResult(-1)
                }else if (res==0 || res==1){
                    savedPosts.put(postid, true)
                    onResult(0)
                }
            }
        }else{
            //remove
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
    /*
    fun getLikes():MutableMap<String, Boolean>{
        return savedPosts
    }

    fun addLike(uid:String, postid:String, onResult:(Int)->Unit){
        if (!savedPosts.contains(postid)){
            LikeDatabase.addLike(uid, postid){ res->
                if (res==-1){
                    //errore nell'inserimento del like
                    onResult(-1)
                }else if (res==0 || res==1){
                    savedPosts.put(postid, true)
                    onResult(0)
                }
            }
        }else{
            //già presente - rimozione
            LikeDatabase.removeLike(uid, postid){ res->
                if (res==0 || res==1){
                    savedPosts.remove(postid)
                    onResult(1)
                }else{
                    onResult(-1)
                }
            }

        }
    }

     */
}