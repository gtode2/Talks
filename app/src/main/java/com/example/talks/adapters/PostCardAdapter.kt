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
import com.example.talks.database.ImageDatabase
import com.example.talks.interfaces.PostHandlerInterface
import com.example.talks.managers.ImageCache
import com.example.talks.managers.ImageManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostCardAdapter(
    private val posts:MutableList<PostData>,
    var pch:PostCardHomepage?,
    private val context: Context,
    private val type: String = "all"
):RecyclerView.Adapter<PostCardAdapter.ViewHolder>(), PostHandlerInterface{
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val usertag = view.findViewById<TextView>(R.id.userTag)
        val posttext = view.findViewById<TextView>(R.id.postText)
        val postImg = view.findViewById<ImageView>(R.id.postImageArea)
        val postLikes = view.findViewById<TextView>(R.id.likeCtr)

        val likebtn = view.findViewById<ImageView>(R.id.likebtn)
        val commentbtn = view.findViewById<ImageView>(R.id.commbtn)
            //comment in all
            //edit in your
        val savebtn = view.findViewById<ImageView>(R.id.savebtn)
            //save in all
            //delete in your

        fun bind(el:PostData){
            usertag.text = "@"+el.uid
            posttext.text = el.post
            postLikes.text = el.likes.toString()


            //verifica immagini
            var cache = ImageCache(20)

            if (!el.image){
                postImg.visibility = View.GONE
            }else{
                //aggiunta immagine
                postImg.visibility=View.VISIBLE
                //postImg.setImageBitmap(R.drawable.placeholder.bit)
                val cachedbmp = cache.get(el.id)
                if (cachedbmp!=null){
                    postImg.setImageBitmap(cachedbmp)
                }else{
                    //richiesta immagine
                    //Dispatchers->thread
                    CoroutineScope(Dispatchers.IO).launch{
                        val b64 = ImageDatabase.get(el.id)
                        val bmp = ImageManager.decode(b64)
                        cache.add(el.id, bmp)
                        val currentPostId = el.id

                        withContext(Dispatchers.Main){
                            //torno in main thread
                            if (adapterPosition!= RecyclerView.NO_POSITION && posts[adapterPosition].id==currentPostId){
                                postImg.setImageBitmap(bmp)
                            }
                        }

                    }
                }
            }

            if (el.isLiked){
                likebtn.imageTintList= ColorStateList.valueOf(ContextCompat.getColor(context, R.color.lime))
            }else{
                likebtn.imageTintList= ColorStateList.valueOf(ContextCompat.getColor(context, R.color.desel))
            }
            if (el.isSaved){
                savebtn.imageTintList= ColorStateList.valueOf(ContextCompat.getColor(context, R.color.lime))
            }else{
                savebtn.imageTintList= ColorStateList.valueOf(ContextCompat.getColor(context, R.color.desel))
            }

            usertag.setOnClickListener{
                pch!!.openUser(el.uid)
            }
            itemView.setOnClickListener{
                pch!!.openPost(el.id)
            }
            likebtn.setOnClickListener{
                pch!!.addLike(el.id)
            }
            commentbtn.setOnClickListener{
                pch!!.openComments(el.id)
            }
            savebtn.setOnClickListener{
                pch!!.savePost(el.id)
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
            .inflate(R.layout.postcard, parent, false)
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
}
