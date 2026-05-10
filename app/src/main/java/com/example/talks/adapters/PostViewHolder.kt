package com.example.talks.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.R
import com.example.talks.data.PostData
import com.example.talks.database.ImageDatabase
import com.example.talks.interfaces.PostCardHomepage
import com.example.talks.singleton.ImageCache
import com.example.talks.managers.ImageManager
import com.example.talks.managers.SourceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import coil.load
import kotlinx.coroutines.Job
import okhttp3.Dispatcher


class PostViewHolder(
    private val view: View,
    private val context: Context,
    private val pch: PostCardHomepage?,
    private val posts: MutableList<PostData>
) : RecyclerView.ViewHolder(view) {

    private val usertag = view.findViewById<TextView>(R.id.userTag)
    private val userImg = view.findViewById<ImageView>(R.id.userImg)

    private val posttitle = view.findViewById<TextView>(R.id.postTitle)
    private val posttext = view.findViewById<TextView>(R.id.postText)
    private val postImg = view.findViewById<ImageView>(R.id.postImageArea)

    private val likeBtn = view.findViewById<LinearLayout>(R.id.likeBtn)
    private val likeIcon = view.findViewById<ImageView>(R.id.likeIcon)
    private val postLikes = view.findViewById<TextView>(R.id.likeCtr)

    private val commBtn = view.findViewById<LinearLayout>(R.id.commBtn)
    private val commentIcon = view.findViewById<ImageView>(R.id.commIcon)
    private val commentCtr = view.findViewById<TextView>(R.id.commCtr)

    private val saveBtn = view.findViewById<LinearLayout>(R.id.saveBtn)
    private val saveIcon = view.findViewById<ImageView>(R.id.saveIcon)
    private val saveTxt = view.findViewById<TextView>(R.id.saveTxt)

    private val src = view.findViewById<ConstraintLayout>(R.id.sourceBlock)
    private val srcImg = view.findViewById<ImageView>(R.id.sourcePreview)
    private val srcUrl = view.findViewById<TextView>(R.id.sourceURL)
    private val srcTitle = view.findViewById<TextView>(R.id.sourceTitle)


    private val scope = CoroutineScope(Dispatchers.Main.immediate)
    private var job: Job?=null


    private var postId:String?=null
    private var userId:String?=null


    fun bind(el: PostData) {
        job?.cancel()

        postId=el.id
        userId=el.uid

        usertag.text = "@${el.uid}"
        posttitle.text = el.title
        posttext.text = el.post
        postLikes.text = el.likes.toString()


        job = scope.launch {
            //immagine
            if (!el.image) {
                postImg.visibility = View.GONE
            }else{
                postImg.visibility = View.VISIBLE


                val bmp = withContext(Dispatchers.IO){ImageCache.get("image${el.id}")}
                if (el.id==postId){
                    if (bmp!=null){
                        postImg.setImageBitmap(bmp)
                    }else{
                        //in caso di errore, se non trova immagine rimuove blocco
                        postImg.visibility = View.GONE
                    }
                }
            }

            //profile picture

            val bmp = withContext(Dispatchers.IO){ImageCache.get("profile${el.uid}")}
            if (el.uid==userId){
                if (bmp!=null){
                    userImg.setImageBitmap(bmp)
                }else{
                    Log.e("AAA", "profile picture non presente ${userId} ", )
                    userImg.setImageDrawable(null)
                }
            }


            //source
            if (el.source==""){
                src.visibility = View.GONE
            }else{
                //verifica url
                val img = withContext(Dispatchers.IO){SourceManager.getFavicon(el.source)}

                if (img.startsWith("/")){
                    //gestione errore
                }else{
                    srcImg.load(img)
                }
                srcUrl.text=el.source
                val title = withContext(Dispatchers.IO){SourceManager.getTitle(el.source)}
                if (title!=null){
                    srcTitle.text=title
                }else{
                    srcTitle.text=""
                }
            }
                    //aggiungere titolo
        }








        /*
        // gestione immagini
        if (!el.image) {
            postImg.visibility = View.GONE
        } else {
            postImg.visibility = View.VISIBLE

            CoroutineScope(Dispatchers.IO).launch {
                val bmp = ImageCache.get("image${el.id}")
                val currentPostId = el.id
                withContext(Dispatchers.Main) {
                    if (adapterPosition != RecyclerView.NO_POSITION && posts[adapterPosition].id == currentPostId) {
                        if (bmp!=null){
                            postImg.setImageBitmap(bmp)
                        }else{
                            //in caso di errore, se non trova immagine rimuove blocco
                            postImg.visibility = View.GONE
                        }

                    }
                }
            }
        }*/

        //gestione profile picture
        /*
        CoroutineScope(Dispatchers.IO).launch {
            val bmp = ImageCache.get("profile${el.uid}")
            val currentPostId = el.id
            withContext(Dispatchers.Main) {
                if (adapterPosition != RecyclerView.NO_POSITION && posts[adapterPosition].id == currentPostId) {
                    if (bmp!=null){
                        userImg.setImageBitmap(bmp)
                    }
                    //se non esiste immagine -> lascia default
                }
            }
        }




        if (el.source==""){
            src.visibility = View.GONE
        }else{
            //check anti recycle?
            CoroutineScope(Dispatchers.IO).launch {
                //verifica url
                val img = SourceManager.getFavicon(el.source)
                withContext(Dispatchers.Main) {
                    if (img.startsWith("/")){
                        //gestione errore
                    }else{
                        srcImg.load(img)

                    }
                    srcUrl.text=el.source
                }

                //aggiungere titolo
            }
        }


         */
        likeIcon.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(context, if (el.isLiked) R.color.lime else R.color.desel)
        )
        saveIcon.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(context, if (el.isSaved) R.color.lime else R.color.desel)
        )


        // click listener
        usertag.setOnClickListener { pch?.openUser(el.uid) }
        itemView.setOnClickListener { pch?.openPost(el.id) }
        likeBtn.setOnClickListener { pch?.addLike(el.id) }
        commBtn.setOnClickListener { pch?.openComments(el.id) }
        saveBtn.setOnClickListener { pch?.savePost(el.id) }
        src.setOnClickListener { pch?.openSource(el.source) }
    }
}