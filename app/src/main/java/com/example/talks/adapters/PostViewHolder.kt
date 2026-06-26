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
import com.example.talks.singleton.ImageCache
import com.example.talks.managers.SourceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import coil.load
import com.example.talks.PostCardHandler
import com.example.talks.database.CommentsDatabase
import com.example.talks.singleton.UserID
import kotlinx.coroutines.Job


open class PostViewHolder(
    private val view: View,
    private val context: Context,
    private val pch: PostCardHandler?
) : RecyclerView.ViewHolder(view) {

    private val posttitle = view.findViewById<TextView>(R.id.postTitle)
    private val posttext = view.findViewById<TextView>(R.id.postText)
    private val postImg = view.findViewById<ImageView>(R.id.postImageArea)

    private val likeBtn = view.findViewById<LinearLayout>(R.id.likeBtn)
    private val likeIcon = view.findViewById<ImageView>(R.id.likeIcon)
    private val postLikes = view.findViewById<TextView>(R.id.likeCtr)

    private val src = view.findViewById<ConstraintLayout>(R.id.sourceBlock)
    private val srcImg = view.findViewById<ImageView>(R.id.sourcePreview)
    private val srcUrl = view.findViewById<TextView>(R.id.sourceURL)
    private val srcTitle = view.findViewById<TextView>(R.id.sourceTitle)

    private val scope = CoroutineScope(Dispatchers.Main.immediate)
    private var job: Job?=null

    private var postId:String?=null
    private var userId:String?=null


    open fun bind(el: PostData, isyour:Boolean=false, isFullScreen:Boolean=false) {
        job?.cancel()

        postId=el.id
        userId=el.uid

        if (!isyour){
            val usertag = view.findViewById<TextView>(R.id.userTag)
            usertag.text = "@${el.uid}"
        }

        posttitle.text = el.title
        posttext.text = el.post
        postLikes.text = el.likes.toString()


        job = scope.launch {
            //immagine
            if (!el.image) {
                postImg.visibility = View.GONE
            }else{
                postImg.visibility = View.VISIBLE


                val bmp = ImageCache.get(el.id, false, el.imgTimestamp)
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

            if (!isyour){
                val userImg = view.findViewById<ImageView>(R.id.userImg)
                val bmp = ImageCache.get(el.uid, true)
                if (el.uid==userId){
                    if (bmp!=null){
                        userImg.setImageBitmap(bmp)
                    }else{
                        userImg.setImageDrawable(null)
                    }
                }
            }


            //source

            if (el.source==""){
                src.visibility = View.GONE
            }else{
                src.visibility = View.VISIBLE
                //verifica url
                val img = SourceManager.getFavicon(el.source)

                if (img==null){
                    srcImg.setImageResource(R.drawable.openlink)
                }else{
                    srcImg.load(img)
                }
                srcUrl.text=el.source
                val title = SourceManager.getTitle(el.source)
                if (title!=null){
                    srcTitle.visibility= View.VISIBLE
                    srcTitle.text=title
                }else{
                    srcTitle.visibility=View.GONE
                }
            }

            //comments

            if (!isyour){
                val commentCtr = view.findViewById<TextView>(R.id.commCtr)
                val cc = CommentsDatabase.count(el.id)
                commentCtr.text = cc.toString()
            }
        }

        if (!isyour){
            val saveIcon = view.findViewById<ImageView>(R.id.saveIcon)
            val saveTxt = view.findViewById<TextView>(R.id.saveTxt)

            val saveBtn = view.findViewById<LinearLayout>(R.id.saveBtn)
            val commBtn = view.findViewById<LinearLayout>(R.id.commBtn)

            val userblock = view.findViewById<LinearLayout>(R.id.userBlock)

            if (el.uid!=UserID.getUID()){
                likeIcon.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, if (el.isLiked) R.color.lime else R.color.desel)
                )
                saveIcon.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, if (el.isSaved) R.color.lime else R.color.desel)
                )
                if (el.isSaved){
                    saveTxt.text = context.getString(R.string.saved_sing)
                }else{
                    saveTxt.text = context.getString(R.string.save)
                }

                saveBtn.setOnClickListener { pch?.savePost(el.id) }
                likeBtn.setOnClickListener { pch?.addLike(el.id) }

            }else{
                likeIcon.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, if (el.isLiked) R.color.lime else R.color.btnlocked)
                )
                saveIcon.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, if (el.isLiked) R.color.lime else R.color.btnlocked)
                )
            }

            userblock.setOnClickListener { pch?.openUser(el.uid) }
            commBtn.setOnClickListener { pch?.openPost(el.id)}


        }else{
            val editbtn = view.findViewById<LinearLayout>(R.id.editBtn)
            val delbtn = view.findViewById<LinearLayout>(R.id.delBtn)

            editbtn.setOnClickListener { pch?.editPost(el.id) }
            delbtn.setOnClickListener { pch?.deletePost(el.id) }
        }

        if (!isFullScreen){
            itemView.setOnClickListener { pch?.openPost(el.id) }
            if (el.uid==UserID.getUID()){
                //"blocco" anche in "your posts"
                likeIcon.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, if (el.isLiked) R.color.lime else R.color.btnlocked)
                )
            }
        }

        src.setOnClickListener { pch?.openSource(el.source) }
    }

}