package com.example.talks.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.R
import com.example.talks.interfaces.PostCard
import com.example.talks.data.PostData
import com.example.talks.data.UserData
import com.example.talks.interfaces.PostHandlerInterface
import com.example.talks.singleton.ImageCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostCardSearchAdapter(
    var posts:MutableList<PostData>?=null,
    var pch:PostCard?,
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
            VIEW_TYPE_POST-> PostViewHolder(view.inflate(R.layout.postcard, parent,false), context, pch, posts!!)
            VIEW_TYPE_USER->UserVH(view.inflate(R.layout.usercard, parent,false))
            VIEW_TYPE_EMPTY->EmptyVH(view.inflate(R.layout.errorpage, parent,false))
            else-> throw IllegalArgumentException("tipo non valido")
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is UserVH->{
                holder.bind()
            }
            is PostViewHolder ->{
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
        val userImg = view.findViewById<ImageView>(R.id.userImg)

        val userBlock = view.findViewById<ConstraintLayout>(R.id.userll)

        private val scope = CoroutineScope(Dispatchers.Main.immediate)
        private var job: Job?=null


        fun bind(){
            job?.cancel()
            usertag.text="@${user!!.Uid}"
            followers.text="${user!!.followers} followers"
            job = scope.launch {
                val bmp = withContext(Dispatchers.IO){ ImageCache.get("profile${user!!.Uid}")}
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

    /*

     */
    inner class EmptyVH(view: View): RecyclerView.ViewHolder(view){

    }




}
