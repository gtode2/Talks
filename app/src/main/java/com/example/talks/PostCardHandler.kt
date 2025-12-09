package com.example.talks

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.talks.adapters.PostCardAdapter
import com.example.talks.database.PostDatabase
import com.example.talks.interfaces.PostCardHomepage
import com.example.talks.interfaces.PostCardYourPosts
import com.example.talks.interfaces.PostHandlerInterface
import com.example.talks.repository.BookmarkRepository
import com.example.talks.repository.LikeRepository

class PostCardHandler(
    private val contextProvider:() ->Context,
    private val adapter:PostHandlerInterface?=null,
    private val openEdit:((String)->Unit)?=null,
    private val openUser:((String)->Unit)?=null
):PostCardHomepage, PostCardYourPosts{

    override fun openPost(postId: String) {
        val intent = Intent(contextProvider(), EmptyActivity::class.java)
        intent.putExtra("id", postId)
        intent.putExtra("screen", "fs")
        contextProvider().startActivity(intent) //creare implementazione di startActivity in fragment / activity

    }

    override fun openComments(postId: String) {

    }

    override fun openUser(userId: String) {
        openUser?.invoke(userId)
    }

    override fun addLike(postId: String) {
        val UID = (contextProvider().applicationContext as AppSettings).getUID()
        if (!UID.isNullOrBlank()){
            LikeRepository.addLike(UID!!,postId){ res->
                //0 = aggiunta eseguita
                //1 = già presente - rimosso
                //-1= errore

                if (res==0){
                    Toast.makeText(contextProvider(), "like aggiunto", Toast.LENGTH_SHORT).show()
                    adapter?.incrLike(postId)
                }else if(res==1){
                    Toast.makeText(contextProvider(), "like rimosso", Toast.LENGTH_SHORT).show()
                    adapter?.decrLike(postId)
                }else if (res==-1){
                    //errore
                    Toast.makeText(contextProvider(), "si è verificato un errore", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun savePost(postId: String) {
        val UID = (contextProvider().applicationContext as AppSettings).getUID()
        if (!UID.isNullOrBlank()){
            BookmarkRepository.savePost(UID, postId){ res->
                if (res==0){
                    Toast.makeText(contextProvider(), "Post salvato", Toast.LENGTH_SHORT).show()
                    adapter?.savePost(postId)
                }else if (res==1){
                    Toast.makeText(contextProvider(), "Post rimosso", Toast.LENGTH_SHORT).show()
                    adapter?.unsavePost(postId)
                }else if (res==-1){
                    Toast.makeText(contextProvider(), "si è verificato un errore", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun editPost(postId: String) {
        openEdit?.invoke(postId)
    }

    override fun deletePost(postId: String) {
        val UID = (contextProvider().applicationContext as AppSettings).getUID()
        if (!UID.isNullOrBlank()){
            AlertDialog.Builder(contextProvider())
                .setTitle("Elimina post")
                .setMessage("Sei sicuro di voler eliminare il post?")
                .setPositiveButton("Elimina"){_,_->
                    PostDatabase.deletePost(UID,postId){ res->
                        if (res==0 || res==1){
                            adapter?.deletePost(postId)
                        }else{
                            Toast.makeText(contextProvider(), "si è verificato un errore nell'eliminazione del post", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("Annulla", null)
                .show()
        }
    }
}