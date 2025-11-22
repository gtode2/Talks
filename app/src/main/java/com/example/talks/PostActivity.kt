package com.example.talks

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
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
import com.example.talks.databinding.AddcommentBinding
import com.example.talks.databinding.PostfullscreenBinding
import com.example.talks.interfaces.Comment
import com.example.talks.interfaces.PostCardHomepage

class PostActivity:AppCompatActivity(), PostCardHomepage, Comment{
    lateinit var binding:PostfullscreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //uso binding per poter rimuovere addcomment da xml quando non loggato
        binding = PostfullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val settings = this.applicationContext as AppSettings
        val UID = settings.getUID()
        val addcomm = binding.addcomment.sendcommbtn

        var adapter:PostAdapter?= null


        if (UID.isNullOrBlank()){
            binding.addcomment.root.visibility=View.GONE
        }

        val postId = intent.getStringExtra("id")
        if (postId.isNullOrBlank()){
            Toast.makeText(this, "post id non inserito", Toast.LENGTH_SHORT)
            finish()
        }

        //request db info post - PostDatabase
        var rvPost = binding.postrv

        rvPost.layoutManager = LinearLayoutManager(this)
        PostDatabase.getPost(postId!!) { postlist ->
            //verifico esistenza post - gestione errore
            if (postlist.isEmpty()){
                //gestire errore - pagina errore xml DA CREARE
            }else{
                //carico info
                val post = postlist[0]
                getComments(postId!!){ comments->
                    adapter = PostAdapter(post, comments,this, this)
                    rvPost.adapter = adapter
                }
            }
        }



        //request db commenti - CommentsDatabase
        //carico commenti da xml - DA CREARE

        addcomm.setOnClickListener{
            //ottenere testo commento
            val commenttext = binding.addcomment.textcomment.text.toString()
            //ottenere id post
            if (!commenttext.isNullOrBlank()){
                addComment(UID!!, commenttext, postId!!){res->
                    if (res==0){
                        binding.addcomment.textcomment.text.clear()
                        adapter!!.addComment(commenttext,UID)

                    }else{
                        Toast.makeText(this, "si è verificato un errore nel caricamento del commento", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this, "scrivi un commento per inviare", Toast.LENGTH_SHORT).show()
            }

        }

    }

    fun getComments(id:String, onResult: (MutableList<CommentData>)->Unit){
        CommentsDatabase.getComments(id){
            comments->onResult(comments)
        }
    }

    fun addComment(uid:String, text:String, post:String, onResult: (Int) -> Unit){
        CommentsDatabase.addComment(uid, text, post){res->
            onResult(res)
        }
    }

    //funzioni interfaccia

    override fun addLike(postId: String) {
        //GESTIONE FIREBASE FREE - unico documento -> letture e scritture limitate
        //SCALABILITÀ LIMITATA -> gestire un documento per ogni like anche per utente




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