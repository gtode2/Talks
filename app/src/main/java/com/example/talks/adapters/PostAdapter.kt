package com.example.talks.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.R
import com.example.talks.data.CommentData
import com.example.talks.interfaces.PostCardHomepage
import com.example.talks.data.PostData
import com.example.talks.interfaces.Comment
import com.example.talks.interfaces.PostHandlerInterface
import com.example.talks.adapters.PostViewHolder

class PostAdapter(
    private val post:PostData,
    private val cm: MutableList<CommentData>,
    private val comdata: Comment,
    var pch:PostCardHomepage?=null,
    private val context: Context

):RecyclerView.Adapter<RecyclerView.ViewHolder>(), PostHandlerInterface{
    companion object {
        private const val VIEW_TYPE_POST=0
        private const val VIEW_TYPE_COMM=1
    }

    /*
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val usertag = view.findViewById<TextView>(R.id.userTag)
        val posttext = view.findViewById<TextView>(R.id.postText)
        val postImg = view.findViewById<ImageView>(R.id.postImageArea)
        val postLikes = view.findViewById<TextView>(R.id.likeCtr)

        val likebtn = view.findViewById<ImageView>(R.id.likebtn)
        val commbtn = view.findViewById<ImageView>(R.id.commentsbtn)
        val savebtn = view.findViewById<ImageView>(R.id.savebtn)

        fun bind(el:PostData){
            usertag.text = "@"+el.uid
            posttext.text = el.post
            postLikes.text = el.likes.toString()



            //verifica presenza link
            //verifica tag

            usertag.setOnClickListener{
                pch!!.openUser(el.uid)
            }
            itemView.setOnClickListener{
                pch!!.openPost(el.id)
            }
            likebtn.setOnClickListener{
                pch!!.addLike(el.id)
            }
            commbtn.setOnClickListener{
                pch!!.openComments(el.id)
            }
            savebtn.setOnClickListener{
                pch!!.savePost(el.id)
            }
        }
    }
    */
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
                holder.bind(post)
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
        notifyItemInserted(1)

    }


    inner class CommentVH(view: View):RecyclerView.ViewHolder(view){
        val usertag = view.findViewById<TextView>(R.id.userTag)
        val commenttext = view.findViewById<TextView>(R.id.commentText)

        fun bind(comment:CommentData){
            usertag.text = "@${comment.uid}"
            commenttext.text = comment.text
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
