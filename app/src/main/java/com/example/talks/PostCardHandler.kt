package com.example.talks

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.talks.database.PostDatabase
import com.example.talks.interfaces.PostHandlerInterface
import com.example.talks.repository.BookmarkRepository
import com.example.talks.repository.LikeRepository
import com.example.talks.singleton.LastPost
import com.example.talks.singleton.UserID

class PostCardHandler(
    private val context:Context,
    private val adapter:PostHandlerInterface?=null,
    private val openEdit:((String)->Unit)?=null,
){

    fun openPost(postId: String) {
        val intent = Intent(context, EmptyActivity::class.java)
        LastPost.addPost(postId)
            intent.putExtra("id", postId)
            intent.putExtra("screen", "fs")
        context.startActivity(intent)

    }
    fun openUser(userId: String) {
        val intent = Intent(context, EmptyActivity::class.java)
            .putExtra("screen","user")
            .putExtra("id",userId)
        context.startActivity(intent)
    }

    fun addLike(postId: String) {
        val uid = UserID.getUID()
        if (!uid.isNullOrBlank()){
            LikeRepository.addLike(uid,postId){ res->
                //0 = aggiunta eseguita
                //1 = già presente - rimosso
                //-1= errore

                when (res) {
                    0 -> adapter?.incrLike(postId)
                    1 -> adapter?.decrLike(postId)
                    -1 -> Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun savePost(postId: String) {
        val uid = UserID.getUID()
        if (!uid.isNullOrBlank()){
            BookmarkRepository.savePost(uid, postId){ res->
                when (res) {
                    0 ->adapter?.savePost(postId)
                    1 -> adapter?.unsavePost(postId)
                    -1 -> Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun editPost(postId: String) {
        openEdit?.invoke(postId)
    }

    fun deletePost(postId: String) {
        val uid = UserID.getUID()
        if (!uid.isNullOrBlank()){
            AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.postDel))
                .setMessage(context.getString(R.string.postDQ))
                .setPositiveButton(context.getString(R.string.delete)){_,_->
                    PostDatabase.deletePost(uid,postId){ res->
                        if (res==0 || res==1){
                            adapter?.deletePost(postId)
                        }else{
                            Toast.makeText(context, context.getString(R.string.errPostDel), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton(context.getString(R.string.cancel), null)
                .show()
        }
    }

    fun openSource(link: String) {
        val uri = when {
            link.startsWith("http://") || link.startsWith("https://") -> link
            link.contains(".") -> "https://$link"
            else -> "https://www.google.com/search?q=${Uri.encode(link)}"
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        context.startActivity(intent)
    }

}