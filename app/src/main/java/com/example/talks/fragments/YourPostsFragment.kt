package com.example.talks.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.talks.AppSettings
import com.example.talks.R
import com.example.talks.data.CommentData
import com.example.talks.database.CommentsDatabase

class YourPostsFragment:Fragment(R.layout.postfullscreen) {


    private var UID:String?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val settings = requireActivity().applicationContext as AppSettings
        if (!settings.getUID().isNullOrBlank()){
            UID = settings.getUID()
        }
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