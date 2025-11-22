package com.example.talks

import android.widget.Toast
import com.example.talks.database.LikeDatabase

object LikeRepository {
    private var likedPosts = mutableMapOf<String, Boolean>()

    fun loadLikes(uid:String){
        //chiamata a LikeDatabase e poi memorizza
        LikeDatabase.getLikes(uid){ likesMap ->
            likedPosts = likesMap
        }
    }

    fun addLike(uid:String, postid:String, onResult:(Int)->Unit){
        if (!likedPosts.contains(postid)){
            LikeDatabase.addLike(uid, postid){res->
                if (res==-1){
                    //errore nell'inserimento del like
                    onResult(-1)
                }else if (res==0 || res==1){
                    likedPosts.put(postid, true)
                    onResult(res)
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
            // chiama removeLike()
            //restituisce 2 se rimosso correttamente

        }
    }
}