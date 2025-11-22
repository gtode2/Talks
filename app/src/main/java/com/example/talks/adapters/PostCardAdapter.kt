package com.example.talks.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.res.colorResource
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.R
import com.example.talks.interfaces.PostCard
import com.example.talks.interfaces.PostCardHomepage
import com.example.talks.data.PostData
import com.example.talks.interfaces.Comment

class PostCardAdapter(
    private val posts:MutableList<PostData>,
    private val pch:PostCardHomepage,
    private val context: Context
):RecyclerView.Adapter<PostCardAdapter.ViewHolder>(){
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


            //verifica immagini
            if (el.image.isNullOrBlank()){
                postImg.visibility = View.GONE
            }

            if (el.isLiked){
                likebtn.imageTintList= ColorStateList.valueOf(ContextCompat.getColor(context, R.color.lime))
            }

            //verifica presenza link
            //verifica tag

            usertag.setOnClickListener{
                pch.openUser(el.uid)
            }
            itemView.setOnClickListener{
                pch.openPost(el.id)
            }
            likebtn.setOnClickListener{
                pch.addLike(el.id)
            }
            commbtn.setOnClickListener{
                pch.openComments(el.id)
            }
            savebtn.setOnClickListener{
                pch.savePost(el.id)
            }
        }
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])
    }
    override fun getItemCount(): Int = posts.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.postcard, parent, false)
        return ViewHolder(view)
    }

    fun incrLike(postid:String){
        val index = posts.indexOfFirst{it.id==postid}
        if (index!=-1){
            posts[index].likes+=1
            posts[index].isLiked=true
        }
        notifyItemChanged(index)
    }
}
