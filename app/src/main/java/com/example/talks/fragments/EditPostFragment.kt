package com.example.talks.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.talks.R

class EditPostFragment:Fragment(R.layout.postcreation) {
    var postId:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postId = arguments?.getString("postid")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}