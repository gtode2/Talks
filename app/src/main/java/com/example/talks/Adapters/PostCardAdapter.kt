package com.example.talks.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.R
import com.example.talks.Interfaces.PostCard
import com.example.talks.Interfaces.PostCardHomepage
import com.example.talks.data.PostData

class PostCardAdapter(
    private val posts:List<PostData>,
    private val pcl:PostCard,
    private val pchl:PostCardHomepage
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


            Log.e("NVNC", "immagine = "+el.image)
            //verifica immagini
            if (el.image.isNullOrBlank()){
                postImg.visibility = View.GONE
            }

            //verifica presenza link
            //verifica tag

            usertag.setOnClickListener{
                pcl.openUser(el.uid)
            }
            itemView.setOnClickListener{
                pcl.openPost(el.id)
            }
            likebtn.setOnClickListener{
                pchl.addLike(el.id)
            }
            commbtn.setOnClickListener{
                pcl.openComments(el.id)
            }
            savebtn.setOnClickListener{
                pchl.savePost(el.id)
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
}
