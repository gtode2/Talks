package com.example.talks.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.EmptyActivity
import com.example.talks.PostCardHandler
import com.example.talks.R
import com.example.talks.adapters.PostAdapter
import com.example.talks.data.CommentData
import com.example.talks.data.PostData
import com.example.talks.database.CommentsDatabase
import com.example.talks.database.PostDatabase
import com.example.talks.interfaces.Comment
import com.example.talks.interfaces.PostCard
import com.example.talks.repository.BookmarkRepository
import com.example.talks.repository.LikeRepository
import com.example.talks.singleton.UserID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostFSFragment:Fragment(R.layout.postfullscreen) {

    var postId:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postId = arguments?.getString("postid")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val UID = UserID.getUID()
        val addCombutton = view.findViewById<ImageView>(R.id.sendcommbtn)
        val addCom = view.findViewById<View>(R.id.addcomment)
        val addComTxt = view.findViewById<EditText>(R.id.textcomment)
        var rvPost = view.findViewById<RecyclerView>(R.id.postrv)


        if (postId.isNullOrBlank()){
            //gestisci errore
        }

        var adapter:PostAdapter?=null

        if (UID.isNullOrBlank()){
            addCom.visibility=View.GONE
        }

        rvPost.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch{
            val postList = withContext(Dispatchers.IO){ PostDatabase.getPost(postId!!)}

            if (postList.isEmpty()){
                Log.e("AAA", "nada, id = ${postId}", )
                //gestione errore - pagina xml DA CREARE
            }else{
                val post = postList[0]

                val comments = withContext(Dispatchers.IO){CommentsDatabase.getComments(postId!!)}

                var liked = LikeRepository.getLikes()
                if (liked.isNotEmpty()){
                    if (liked.containsKey(post.id)){
                        post.isLiked=true
                    }
                }
                val saved = BookmarkRepository.getSaved()
                if (saved.isNotEmpty()){
                    if (saved.containsKey(post.id)){
                        post.isSaved=true
                    }
                }
                adapter = PostAdapter(
                    post,
                    comments,
                    null,
                    requireContext()
                )

                val handler = PostCardHandler(
                    contextProvider = {requireContext()},
                    adapter=adapter,
                    null,
                    openUser = {userid->openUser(userid)}
                )

                adapter!!.pch = handler
                rvPost.adapter = adapter
                }

        }

        addCombutton.setOnClickListener {
            val commenttext = addComTxt.text.toString()
            if (!commenttext.isNullOrBlank()){
                addComment(UID!!, commenttext, postId!!){res->
                    if (res==0){
                        addComTxt.text.clear()
                        adapter!!.addComment(commenttext, UID)
                    }else{
                        Toast.makeText(requireContext(), "errore", Toast.LENGTH_SHORT).show()
                    }

                }
            }else{
                Toast.makeText(requireContext(), "inserire un testo", Toast.LENGTH_SHORT).show()
            }
        }

    }




    //spostare addcomment
    fun addComment(uid:String, text:String, post:String, onResult: (Int) -> Unit){
        CommentsDatabase.addComment(uid, text, post){res->
            onResult(res)
        }
    }





    fun openUser(uid:String) {
        val intent = Intent(requireContext(), EmptyActivity::class.java)
            .putExtra("screen","user")
            .putExtra("id",uid)
        startActivity(intent)    }


}