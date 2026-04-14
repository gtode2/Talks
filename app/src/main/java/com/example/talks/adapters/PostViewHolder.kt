package com.example.talks.adapters

import android.content.Context
import android.content.res.ColorStateList
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
import com.example.talks.managers.ImageCache
import com.example.talks.managers.ImageManager
import com.example.talks.managers.SourceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import coil.load
import okhttp3.Dispatcher


class PostViewHolder(
    private val view: View,
    private val context: Context,
    private val pch: PostCardHomepage?,
    private val posts: MutableList<PostData>
) : RecyclerView.ViewHolder(view) {

    private val usertag = view.findViewById<TextView>(R.id.userTag)
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


    private val cache = ImageCache(20)

    fun bind(el: PostData) {
        usertag.text = "@${el.uid}"
        posttitle.text = el.title
        posttext.text = el.post
        postLikes.text = el.likes.toString()

        // gestione immagini
        if (!el.image) {
            postImg.visibility = View.GONE
        } else {
            postImg.visibility = View.VISIBLE
            val cachedBmp = cache.get(el.id)
            if (cachedBmp != null) {
                postImg.setImageBitmap(cachedBmp)
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    val b64 = ImageDatabase.get(el.id)
                    val bmp = ImageManager.decode(b64)
                    cache.add(el.id, bmp)
                    val currentPostId = el.id
                    withContext(Dispatchers.Main) {
                        if (adapterPosition != RecyclerView.NO_POSITION && posts[adapterPosition].id == currentPostId) {
                            postImg.setImageBitmap(bmp)
                        }
                    }
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