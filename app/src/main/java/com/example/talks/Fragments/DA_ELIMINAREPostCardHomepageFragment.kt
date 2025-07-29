package com.example.talks.Fragments

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.talks.Interfaces.PostCard
import com.example.talks.Interfaces.PostCardHomepage

class DA_ELIMINAREPostCardHomepageFragment:Fragment(), PostCard, PostCardHomepage{


    //////
    override fun openComments(postId: String) {
        Toast.makeText(context, postId, Toast.LENGTH_SHORT).show()
    }

    override fun openUser(userId: String) {

    }

    override fun openPost(postId: String) {

    }

    override fun addLike(postId: String) {

    }

    override fun savePost(postId: String) {

    }
}