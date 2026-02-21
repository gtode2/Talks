package com.example.talks.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.singleton.AppSettings
import com.example.talks.PostCardHandler
import com.example.talks.R
import com.example.talks.adapters.PostCardAdapter
import com.example.talks.adapters.YourPostCardAdapter
import com.example.talks.data.CommentData
import com.example.talks.database.CommentsDatabase
import com.example.talks.database.PostDatabase
import com.example.talks.singleton.UserID

class YourPostsFragment:Fragment(R.layout.yourposts) {
    var adapter: YourPostCardAdapter?=null
    private var uid:String?=null
    var Fragview:View?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Fragview=view
        init()
    }

    override fun onResume() {
        super.onResume()
        init()
    }
    fun init(){
        val settings = requireActivity().applicationContext as AppSettings
        uid = UserID.getUID()
        if (uid.isNullOrBlank()){
            Toast.makeText(context, "Si è verificato un problema, accedere di nuovo e riprovare", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }
        val rv = Fragview!!.findViewById<RecyclerView>(R.id.yourRV)
        rv.layoutManager = LinearLayoutManager(context)

        PostDatabase.getPosts("user", uid!!){postList->
            //passare parametro per generazione pagina senza like o save ma solo edit e remove
            Toast.makeText(context, "${postList.size}", Toast.LENGTH_SHORT).show()
            adapter = YourPostCardAdapter(
                postList.toMutableList(),
                null,
                requireContext()
            )
            val handler = PostCardHandler(
                contextProvider = {requireContext()},
                adapter=adapter,
                openEdit = {postId->editPost(postId)}
            )
            adapter!!.pch=handler
            rv.adapter=adapter

        }

    }
    fun editPost(postId:String){
        parentFragmentManager.beginTransaction()
            .replace(R.id.emptyframe, EditPostFragment().apply {
                arguments=Bundle().apply {
                    putString("id", postId)
                }
            })
            .commit()
    }



































    fun getComments(id:String, onResult: (MutableList<CommentData>)->Unit){
        CommentsDatabase.getComments(id){
                comments->onResult(comments)
        }
    }

    fun addComment(uid:String, text:String, post:String, onResult: (Int) -> Unit){
        CommentsDatabase.addComment(uid, text, post){ res->
            onResult(res)
        }
    }

}