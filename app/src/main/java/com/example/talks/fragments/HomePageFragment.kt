package com.example.talks.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.adapters.PostCardAdapter
import com.example.talks.interfaces.PostCard
import com.example.talks.interfaces.PostCardHomepage
import com.example.talks.PostActivity
import com.example.talks.R
import com.example.talks.database.PostDatabase

class HomePageFragment:Fragment(R.layout.homepage), PostCardHomepage {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var recyclerViewHomepage = view.findViewById<RecyclerView>(R.id.homepageRV)
        recyclerViewHomepage.layoutManager = LinearLayoutManager(context)
        PostDatabase.getPosts {postList->
            recyclerViewHomepage.adapter = PostCardAdapter(postList, this )
            for (post in postList){
                Log.e("TEST", post.title)
            }
        }

    }

    override fun openPost(postId: String) {
        Log.e("NVNC", "caricamento nuova activity")
        val intent = Intent(requireContext(), PostActivity::class.java)
        Log.e("NVNC", "intent creato")
        intent.putExtra("id", postId)
        startActivity(intent)

    }
    override fun openComments(postId: String) {

    }

    override fun openUser(userId: String) {

    }

    override fun addLike(postId: String) {

    }

    override fun savePost(postId: String) {

    }
}