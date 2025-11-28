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

class PostFSFragment:Fragment(R.layout.postfullscreen) {
    var postId:String? = null
    var adapter:PostAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postId = arguments?.getString("postid")
    }
    private val handler by lazy{
        PostCardHandler(
            contextProvider = {requireContext()},
            adapter = adapter
        )
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
                /*
                getComments(postId!!){comments->
                    adapter=PostAdapter(post, comments, aaaaa, bbbbb)
                }*/
            }


        }
    }
    fun getComments(id:String, onResult: (MutableList<CommentData>)->Unit){
        CommentsDatabase.getComments(id){
                comments->onResult(comments)
        }
    }
}