package com.example.talks.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.AppSettings
import com.example.talks.PostCardHandler
import com.example.talks.R
import com.example.talks.adapters.PostAdapter
import com.example.talks.data.CommentData
import com.example.talks.database.CommentsDatabase
import com.example.talks.database.PostDatabase
import com.example.talks.interfaces.Comment
import com.example.talks.repository.BookmarkRepository
import com.example.talks.repository.LikeRepository

class PostFSFragment:Fragment(R.layout.postfullscreen), Comment {
    var postId:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postId = arguments?.getString("postid")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val settings = requireActivity().applicationContext as AppSettings
        val UID = settings.getUID()
        val addCombutton = view.findViewById<ImageView>(R.id.sendcommbtn)
        val addCom = view.findViewById<View>(R.id.addcomment)
        var rvPost = view.findViewById<RecyclerView>(R.id.postrv)


        if (postId.isNullOrBlank()){
            //gestisci errore
        }

        var adapter:PostAdapter?=null

        if (UID.isNullOrBlank()){
            addCom.visibility=View.GONE
        }

        rvPost.layoutManager = LinearLayoutManager(requireContext())
        PostDatabase.getPost(postId!!){ postList->
            if (postList.isEmpty()){
                //gestione errore - pagina xml DA CREARE
            }else{
                //carico info
                val post = postList[0]

                getComments(postId!!){comments->
                    adapter = PostAdapter(
                        post,
                        comments,
                        this,
                        null,
                        requireContext()
                    )
                    val handler = PostCardHandler(
                        contextProvider = {requireContext()},
                        adapter=adapter
                    )
                    adapter!!.pch = handler
                    var liked = LikeRepository.getLikes()
                    if (!liked.isEmpty()){
                        if (liked.containsKey(post.id)){
                            post.isLiked=true
                        }
                    }
                    val saved = BookmarkRepository.getSaved()
                    if (!saved.isEmpty()){
                        if (saved.containsKey(post.id)){
                            post.isSaved=true
                        }
                    }
                    rvPost.adapter = adapter

                }
            }


        }
    }
    fun getComments(id:String, onResult: (MutableList<CommentData>)->Unit){
        CommentsDatabase.getComments(id){
                comments->onResult(comments)
        }
    }

    override fun openUser() {
        TODO("Not yet implemented")
    }
}