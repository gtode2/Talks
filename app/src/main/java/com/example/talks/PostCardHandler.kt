package com.example.talks

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.talks.database.PostDatabase
import com.example.talks.interfaces.PostCard
import com.example.talks.interfaces.PostHandlerInterface
import com.example.talks.repository.BookmarkRepository
import com.example.talks.repository.LikeRepository
import com.example.talks.singleton.LastPost
import com.example.talks.singleton.UserID

class PostCardHandler(
    private val contextProvider:() ->Context,
    private val adapter:PostHandlerInterface?=null,
    private val openEdit:((String)->Unit)?=null,
    private val openUser:((String)->Unit)?=null
):PostCard{

    override fun openPost(postId: String) {
        val intent = Intent(contextProvider(), EmptyActivity::class.java)
        LastPost.addPost(postId)
        intent.putExtra("id", postId)
        intent.putExtra("screen", "fs")
        contextProvider().startActivity(intent)

    }


    override fun openUser(userId: String) {
        openUser?.invoke(userId)
    }

    override fun addLike(postId: String) {
        val UID = UserID.getUID()
        if (!UID.isNullOrBlank()){
            LikeRepository.addLike(UID,postId){ res->
                //0 = aggiunta eseguita
                //1 = già presente - rimosso
                //-1= errore

                if (res==0){
                    adapter?.incrLike(postId)
                }else if(res==1){
                    adapter?.decrLike(postId)
                }else if (res==-1){
                    Toast.makeText(contextProvider(), contextProvider().getString(R.string.error), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun savePost(postId: String) {
        val UID = UserID.getUID()
        if (!UID.isNullOrBlank()){
            BookmarkRepository.savePost(UID, postId){ res->
                if (res==0){
                    adapter?.savePost(postId)
                }else if (res==1){
                    adapter?.unsavePost(postId)
                }else if (res==-1){
                    Toast.makeText(contextProvider(), contextProvider().getString(R.string.error), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun editPost(postId: String) {
        openEdit?.invoke(postId)
    }

    override fun deletePost(postId: String) {
        val UID = UserID.getUID()
        if (!UID.isNullOrBlank()){
            AlertDialog.Builder(contextProvider())
                .setTitle(contextProvider().getString(R.string.postDel))
                .setMessage(contextProvider().getString(R.string.postDQ))
                .setPositiveButton(contextProvider().getString(R.string.delete)){_,_->
                    PostDatabase.deletePost(UID,postId){ res->
                        if (res==0 || res==1){
                            adapter?.deletePost(postId)
                        }else{
                            Toast.makeText(contextProvider(), contextProvider().getString(R.string.errPostDel), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton(contextProvider().getString(R.string.cancel), null)
                .show()
        }
    }

    override fun openSource(link: String) {
        val uri = when {
            link.startsWith("http://") || link.startsWith("https://") -> link
            link.contains(".") -> "https://$link"
            else -> "https://www.google.com/search?q=${Uri.encode(link)}"
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        contextProvider().startActivity(intent)
    }
}