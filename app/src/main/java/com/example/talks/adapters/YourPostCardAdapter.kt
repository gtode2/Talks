package com.example.talks.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.R
import com.example.talks.data.PostData
import com.example.talks.interfaces.PostCard
import com.example.talks.interfaces.PostHandlerInterface

class YourPostCardAdapter(
    private val posts:MutableList<PostData>,
    var pc: PostCard?,
    private val context: Context,
):RecyclerView.Adapter<PostViewHolder>(), PostHandlerInterface{
    /*inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
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
    }*/
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position], true)
    }
    override fun getItemCount(): Int = posts.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.postcardyour, parent, false)
        return PostViewHolder(view, context, pc, posts)
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
