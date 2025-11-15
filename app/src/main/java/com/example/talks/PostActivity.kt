package com.example.talks

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.adapters.PostAdapter
import com.example.talks.adapters.PostCardAdapter
import com.example.talks.data.CommentData
import com.example.talks.database.CommentsDatabase
import com.example.talks.database.PostDatabase
import com.example.talks.interfaces.Comment
import com.example.talks.interfaces.PostCardHomepage

class PostActivity:AppCompatActivity(), PostCardHomepage, Comment{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.postfullscreen)

        val postId = intent.getStringExtra("id")
        if (postId.isNullOrBlank()){
            Toast.makeText(this, "post id non inserito", Toast.LENGTH_SHORT)
            finish()
        }

        //request db info post - PostDatabase
        var rvPost = findViewById<RecyclerView>(R.id.postrv)
        rvPost.layoutManager = LinearLayoutManager(this)
        PostDatabase.getPost(postId!!) { postlist ->
            //verifico esistenza post - gestione errore
            if (postlist.isEmpty()){
                //gestire errore - pagina errore xml DA CREARE
            }else{
                //carico info
                val post = postlist[0]
                getComments(postId!!){ comments->

                    rvPost.adapter = PostAdapter(post, comments,this, this)
                }
            }
        }



        //inserisco addcomment.xml
        //request db commenti - CommentsDatabase
        //carico commenti da xml - DA CREARE


    }

    fun getComments(id:String, onResult: (List<CommentData>)->Unit){
        CommentsDatabase.getComments(id){
            comments->onResult(comments)
        }
    }

    //funzioni interfaccia

    override fun addLike(postId: String) {
        TODO("Not yet implemented")
    }

    override fun savePost(postId: String) {
        TODO("Not yet implemented")
    }

    override fun openPost(postId: String) {
        TODO("Not yet implemented")
    }

    override fun openUser(userId: String) {
        TODO("Not yet implemented")
    }

    override fun openComments(postId: String) {
        TODO("Not yet implemented")
    }

    override fun openUser() {
        TODO("Not yet implemented")
    }

}