package com.example.talks.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.R
import com.example.talks.data.CommentData
import com.example.talks.interfaces.PostCard
import com.example.talks.data.PostData
import com.example.talks.interfaces.PostHandlerInterface
import com.example.talks.singleton.ImageCache
import com.example.talks.singleton.LastPost
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostAdapter(
    private val post:PostData,
    private val cm: MutableList<CommentData>,
    var pch:PostCard?=null,
    private val context: Context,

):RecyclerView.Adapter<RecyclerView.ViewHolder>(), PostHandlerInterface{
    //Post Full Screen
    companion object {
        private const val VIEW_TYPE_POST=0
        private const val VIEW_TYPE_COMM=1
    }

    override fun getItemCount(): Int = cm.size+1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
        return when(viewType){
            VIEW_TYPE_POST-> PostViewHolder(view.inflate(R.layout.postcard, parent,false), context, pch, mutableListOf(post))
            VIEW_TYPE_COMM->CommentVH(view.inflate(R.layout.commentblock, parent,false))
            else-> throw IllegalArgumentException("tipo non valido")
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is PostViewHolder ->{
                holder.bind(post, false, true)
            }

            is CommentVH->{
                val comment = cm[position-1]
                holder.bind(comment)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(position){
            0-> VIEW_TYPE_POST
            else-> VIEW_TYPE_COMM
        }
    }

    fun addComment(commtext:String, user:String){
        val newComm = CommentData(
            text = commtext,
            uid = user
        )
        cm.add(0,newComm)
        LastPost.incrCC()
        notifyItemInserted(1)
        notifyItemChanged(0)

    }


    class CommentVH(view: View):RecyclerView.ViewHolder(view){
        val usertag = view .findViewById<TextView>(R.id.commentUserTag)
        val commenttext = view.findViewById<TextView>(R.id.commentTxt)
        val userImg = view.findViewById<ShapeableImageView>(R.id.userImg)

        fun bind(comment:CommentData){
            usertag.text = "@${comment.uid}"
            commenttext.text = comment.text
            CoroutineScope(Dispatchers.IO).launch {
                val bmp = ImageCache.get("profile${comment.uid}")
                withContext(Dispatchers.Main){
                    if (bmp!=null){
                        userImg.setImageBitmap(bmp)
                    }
                }
            }
        }
    }

    override fun incrLike(postId: String) {
        post.likes+=1
        post.isLiked=true
        notifyItemChanged(0)
    }

    override fun decrLike(postId: String) {
        post.likes-=1
        post.isLiked=false
        notifyItemChanged(0)
    }

    override fun savePost(postId: String) {
        post.isSaved=true
        notifyItemChanged(0)
    }

    override fun unsavePost(postId: String) {
        post.isSaved=false
        notifyItemChanged(0)
    }



}
