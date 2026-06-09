package com.example.talks.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.R
import com.example.talks.data.PostData
import com.example.talks.data.UserData
import com.example.talks.interfaces.PostCard
import com.example.talks.interfaces.PostHandlerInterface
import com.example.talks.repository.FollowRepository
import com.example.talks.singleton.ImageCache
import com.example.talks.singleton.UserID
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserPageAdapter(
    private val posts:MutableList<PostData>,
    var pch: PostCard?,
    val context: Context,
    private val user: UserData
): RecyclerView.Adapter<RecyclerView.ViewHolder>(), PostHandlerInterface{
    companion object{
        private const val VIEW_TYPE_USER=0
        private const val VIEW_TYPE_POST=1
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is PostViewHolder->{
                holder.bind(posts[position-1])
            }

            is UserVH->{
                holder.bind(user)
            }
        }
    }

    override fun getItemCount(): Int = posts.size+1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
        return when(viewType){
            VIEW_TYPE_USER-> UserVH(view.inflate(R.layout.userblock, parent, false), context)
            VIEW_TYPE_POST-> PostViewHolder(view.inflate(R.layout.postcard, parent, false), context, pch, posts)
            else-> throw IllegalArgumentException("tipo non valido")
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when(position){
            0-> VIEW_TYPE_USER
            else-> VIEW_TYPE_POST

        }
    }

    class UserVH(view: View, context:Context):RecyclerView.ViewHolder(view){
        val userns = view.findViewById<TextView>(R.id.userns)
        val fw = view.findViewById<TextView>(R.id.followers)
        val fwd = view.findViewById<TextView>(R.id.followed)
        val userImg = view.findViewById<ShapeableImageView>(R.id.userImg)
        val followBtn = view.findViewById<ConstraintLayout>(R.id.followbutton)
        val followTxt = view.findViewById<TextView>(R.id.followTxt)
        val followImg = view.findViewById<ImageView>(R.id.followImg)


        val scope = CoroutineScope(Dispatchers.Main.immediate)
        private var job: Job?=null

        fun bind(user:UserData){
            job?.cancel()

            if (UserID.getUID().isNullOrBlank() || UserID.getUID() == user.Uid){
                //rimuovo bottone se sloggato o se stesso
                followBtn.visibility=View.GONE
            }else if (FollowRepository.isFollowed(user.Uid)){
                followTxt.text="Following"
                followBtn.background= ContextCompat.getDrawable(itemView.context, R.drawable.switchbggreen)
                followImg.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.person_remove))
            }else{
                followTxt.text="Follow"
                followBtn.background= ContextCompat.getDrawable(itemView.context, R.drawable.switchbg)
                followImg.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.person_add))
            }

            job = scope.launch {
                val bmp = withContext(Dispatchers.IO){ ImageCache.get("profile${user.Uid}") }
                if (bmp!=null){
                    userImg.setImageBitmap(bmp)
                }else{
                    userImg.setImageDrawable(null)
                }
            }
            fw.text = user.followers.toString()
            fwd.text = user.followed.toString()
            userns.text = "${user.name} ${user.surname}"

            followBtn.setOnClickListener {
                scope.launch {
                    val res = withContext(Dispatchers.IO){ FollowRepository.addFollow(user.Uid) }
                    if (res>=0){
                        //modifico stato
                        if (res<2){
                            followTxt.text="Following"
                            followBtn.background= ContextCompat.getDrawable(itemView.context, R.drawable.switchbggreen)
                            followImg.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.person_remove))
                            if(res==0) fw.text = (fw.text.toString().toInt()+1).toString()
                        }else{
                            followTxt.text="Follow"
                            followBtn.background= ContextCompat.getDrawable(itemView.context, R.drawable.switchbg)
                            followImg.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.person_add))
                            if(res==3) fw.text = (fw.text.toString().toInt()-1).toString()
                        }
                    }else{
                        Toast.makeText(itemView.context, "Si è verificato un errore", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }

    override fun incrLike(postId:String){
        val index = posts.indexOfFirst{it.id==postId}
        if (index!=-1){
            posts[index].likes+=1
            posts[index].isLiked=true
        }
        notifyItemChanged(index+1)
    }
    override fun decrLike(postId:String){
        val index = posts.indexOfFirst {it.id==postId}
        if (index!=-1){
            posts[index].likes-=1
            posts[index].isLiked=false
        }
        notifyItemChanged(index+1)
    }

    override fun savePost(postId: String) {
        val index = posts.indexOfFirst { it.id==postId}
        if (index!=-1){
            posts[index].isSaved=true
        }
        notifyItemChanged(index+1)
    }
    override fun unsavePost(postId: String) {
        val index = posts.indexOfFirst { it.id==postId}
        if (index!=-1){
            posts[index].isSaved=false
        }
        notifyItemChanged(index+1)
    }

    fun commCount(postId: String){
        val index = posts.indexOfFirst { it.id==postId}
        notifyItemChanged(index+1)
    }
}