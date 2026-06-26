package com.example.talks.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.PostCardHandler
import com.example.talks.R
import com.example.talks.data.PostData
import com.example.talks.data.UserData
import com.example.talks.interfaces.PostHandlerInterface
import com.example.talks.singleton.ImageCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchAdapter(
    var posts:MutableList<PostData>?=null,
    var pch: PostCardHandler?,
    val context: Context,
    var user: UserData? = null
):RecyclerView.Adapter<RecyclerView.ViewHolder>(), PostHandlerInterface{
    companion object{
        private const val VIEW_TYPE_USER=0
        private const val VIEW_TYPE_POST=1
    }
    override fun getItemCount(): Int = size()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
        return when(viewType){
            VIEW_TYPE_POST-> PostViewHolder(view.inflate(R.layout.postcard, parent,false), context, pch)
            VIEW_TYPE_USER->UserVH(view.inflate(R.layout.usercard, parent,false))
            else-> throw IllegalArgumentException("tipo non valido")
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is UserVH->{
                holder.bind()
            }
            is PostViewHolder ->{
                val index = if (user!=null) position-1 else position
                holder.bind(posts!![index])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (user!=null){
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
        val userImg = view.findViewById<ImageView>(R.id.userImg)

        val userBlock = view.findViewById<ConstraintLayout>(R.id.userll)

        private val scope = CoroutineScope(Dispatchers.Main.immediate)
        private var job: Job?=null


        fun bind(){
            job?.cancel()
            usertag.text="@${user!!.Uid}"
            followers.text="${user!!.followers} followers"
            job = scope.launch {
                val bmp = ImageCache.get(user!!.Uid, true)
                if (bmp!=null){
                    userImg.setImageBitmap(bmp)
                }else{
                    userImg.setImageDrawable(null)
                }

            }

            userBlock.setOnClickListener {
                pch?.openUser(user!!.Uid)
            }
        }
    }
    fun commCount(postId: String){
        val postIndex = posts?.indexOfFirst { it.id == postId } ?: return
        val adapterIndex = if (user != null) postIndex + 1 else postIndex
        notifyItemChanged(adapterIndex)
    }


    override fun incrLike(postId:String){
        var index = posts?.indexOfFirst{it.id==postId} ?: return
        posts!![index].likes+=1
        posts!![index].isLiked=true
        if (user!= null) index++
        notifyItemChanged(index)
    }

    override fun decrLike(postId:String){
        var index = posts?.indexOfFirst {it.id==postId} ?: return
        posts!![index].likes-=1
        posts!![index].isLiked=false
        if (user!= null) index++
        notifyItemChanged(index)
    }

    override fun savePost(postId: String) {
        var index = posts?.indexOfFirst { it.id==postId} ?:return
        posts!![index].isSaved=true
        if (user!= null) index++
        notifyItemChanged(index)
    }

    override fun unsavePost(postId: String) {
        var index = posts?.indexOfFirst { it.id==postId} ?:return
        posts!![index].isSaved=false
        if (user!= null) index++
        notifyItemChanged(index)
    }

}
