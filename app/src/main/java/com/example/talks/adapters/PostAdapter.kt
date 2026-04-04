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
            VIEW_TYPE_POST->PostVH(view.inflate(R.layout.postcard, parent,false))
            VIEW_TYPE_COMM->CommentVH(view.inflate(R.layout.commentblock, parent,false))
            else-> throw IllegalArgumentException("tipo non valido")
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is PostVH ->{
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

    inner class PostVH(view: View):RecyclerView.ViewHolder(view){
        val usertag = view.findViewById<TextView>(R.id.userTag)
        val posttitle = view.findViewById<TextView>(R.id.postTitle)
        val posttext = view.findViewById<TextView>(R.id.postText)
        val postImg = view.findViewById<ImageView>(R.id.postImageArea)
        val likebtn = view.findViewById<ImageView>(R.id.likeIcon)
        val likes = view.findViewById<TextView>(R.id.likeCtr)
        val commentbtn = view.findViewById<ImageView>(R.id.commIcon)
        val savebtn = view.findViewById<ImageView>(R.id.saveIcon)

        fun bind(post:PostData){
            usertag.text=post.uid
            posttitle.text=post.title
            posttext.text=post.post
            if (!post.image){
                postImg.visibility=View.GONE
            }

            if (post.isLiked){
                likebtn.imageTintList= ColorStateList.valueOf(ContextCompat.getColor(context, R.color.lime))
            }else{
                likebtn.imageTintList= ColorStateList.valueOf(ContextCompat.getColor(context, R.color.desel))
            }

            if (post.isSaved){
                savebtn.imageTintList= ColorStateList.valueOf(ContextCompat.getColor(context, R.color.lime))
            }else{
                savebtn.imageTintList= ColorStateList.valueOf(ContextCompat.getColor(context, R.color.desel))
            }

            //gestire link
            likes.text=post.likes.toString()

            usertag.setOnClickListener{
                pch!!.openUser(post.uid)
            }
            itemView.setOnClickListener{
                //pch!!.openPost(el.id)
            }
            likebtn.setOnClickListener{
                pch!!.addLike(post.id)
            }
            /*commbtn.setOnClickListener{
                //pch!!.openComments(el.id)
            }*/
            savebtn.setOnClickListener{
                pch!!.savePost(post.id)
            }

        }
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
