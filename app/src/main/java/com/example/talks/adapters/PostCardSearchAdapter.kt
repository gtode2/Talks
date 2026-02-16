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
import com.example.talks.data.UserData
import com.example.talks.interfaces.PostHandlerInterface

class PostCardSearchAdapter(
    var posts:MutableList<PostData>?=null,
    var pch:PostCardHomepage?,
    private val context: Context,
    var user: UserData? = null
):RecyclerView.Adapter<RecyclerView.ViewHolder>(), PostHandlerInterface{
    companion object{
        private const val VIEW_TYPE_USER=0
        private const val VIEW_TYPE_POST=1
        private const val VIEW_TYPE_EMPTY=2
    }
    override fun getItemCount(): Int = size()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
        return when(viewType){
            VIEW_TYPE_POST->PostVH(view.inflate(R.layout.postcard, parent,false))
            VIEW_TYPE_USER->UserVH(view.inflate(R.layout.usercard, parent,false))
            VIEW_TYPE_EMPTY->EmptyVH(view.inflate(R.layout.searchnoitemfound, parent,false))
            else-> throw IllegalArgumentException("tipo non valido")
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is UserVH->{
                holder.bind()
            }
            is PostVH ->{
                var index = if (user!=null) position-1 else position
                holder.bind(posts!![index])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (posts==null && user==null){
            return VIEW_TYPE_EMPTY
        }else if (user!=null){
            return when(position){
                0-> VIEW_TYPE_USER
                else-> VIEW_TYPE_POST
            }
        }else{
            return VIEW_TYPE_POST
        }
    }

    private fun size():Int{
        val local = posts

        if (user==null) {
            if (posts==null){
                return 1
            }else{
                return local!!.size
            }
        }else{
            return local!!.size+1
        }
    }
    inner class UserVH(view: View):RecyclerView.ViewHolder(view){
        val usertag = view.findViewById<TextView>(R.id.searchUserTag)
        val followers = view.findViewById<TextView>(R.id.searchFw)

        fun bind(){
            usertag.text=user!!.Uid
            followers.text=user!!.followers.toString()

        }
    }
    inner class PostVH(view: View):RecyclerView.ViewHolder(view){
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
            if (el.image.isBlank()){
                postImg.visibility = View.GONE
            }

            if (el.isLiked){
                likebtn.imageTintList= ColorStateList.valueOf(ContextCompat.getColor(context, R.color.lime))
            }else{
                likebtn.imageTintList= ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black))
            }
            if (el.isSaved){
                savebtn.imageTintList= ColorStateList.valueOf(ContextCompat.getColor(context, R.color.lime))
            }else{
                savebtn.imageTintList= ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black))
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
    inner class EmptyVH(view: View): RecyclerView.ViewHolder(view){

    }




}
