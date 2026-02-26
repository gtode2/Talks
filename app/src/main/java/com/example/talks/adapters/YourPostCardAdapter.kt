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
import com.example.talks.interfaces.PostCardHomepage
import com.example.talks.data.PostData
import com.example.talks.interfaces.PostCardYourPosts
import com.example.talks.interfaces.PostHandlerInterface

class YourPostCardAdapter(
    private val posts:MutableList<PostData>,
    var pch:PostCardYourPosts?,
    private val context: Context,
):RecyclerView.Adapter<YourPostCardAdapter.ViewHolder>(), PostHandlerInterface{
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val usertag = view.findViewById<TextView>(R.id.userTag)
        val posttext = view.findViewById<TextView>(R.id.postText)
        val postImg = view.findViewById<ImageView>(R.id.postImageArea)
        val postLikes = view.findViewById<TextView>(R.id.likeCtr)

        val likebtn = view.findViewById<ImageView>(R.id.likebtn)
        val editbtn = view.findViewById<ImageView>(R.id.editbtn)
        val delbtn = view.findViewById<ImageView>(R.id.delbtn)

        fun bind(el:PostData){
            usertag.text = "@"+el.uid
            posttext.text = el.post
            postLikes.text = el.likes.toString()


            //verifica immagini
            if (!el.image){
                postImg.visibility = View.GONE
            }


            usertag.setOnClickListener{
                pch!!.openUser(el.uid)
            }
            itemView.setOnClickListener{
                pch!!.openPost(el.id)
            }
            editbtn.setOnClickListener{
                pch!!.editPost(el.id)
            }
            delbtn.setOnClickListener{
                pch!!.deletePost(el.id)
            }

            //verifica presenza link
            //verifica tag

            
        }
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])
    }
    override fun getItemCount(): Int = posts.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.postcardyour, parent, false)
        return ViewHolder(view)
    }

    override fun incrLike(postId:String){
        val index = posts.indexOfFirst{it.id==postId}
        if (index!=-1){
            posts[index].likes+=1
            posts[index].isLiked=true
        }
        notifyItemChanged(index)
    }
    override fun decrLike(postId:String){
        val index = posts.indexOfFirst {it.id==postId}
        if (index!=-1){
            posts[index].likes-=1
            posts[index].isLiked=false
        }
        notifyItemChanged(index)
    }

    override fun savePost(postId: String) {
        val index = posts.indexOfFirst { it.id==postId}
        if (index!=-1){
            posts[index].isSaved=true
        }
        notifyItemChanged(index)
    }
    override fun unsavePost(postId: String) {
        val index = posts.indexOfFirst { it.id==postId}
        if (index!=-1){
            posts[index].isSaved=false
        }
        notifyItemChanged(index)
    }


    override fun editPost(postId: String) {
        //
    }
    override fun deletePost(postId: String) {
        val index = posts.indexOfFirst { it.id==postId }
        posts.removeIf { it.id===postId }
        notifyItemRemoved(index)
    }
}
