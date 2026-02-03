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
import com.example.talks.database.PostDatabase
import com.example.talks.interfaces.PostCardHomepage
import com.example.talks.repository.BookmarkRepository
import com.example.talks.repository.LikeRepository

class SavedPostsFragment:Fragment(R.layout.savedposts) {
    var adapter: PostCardAdapter?=null
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
        uid = settings.getUID()
        if (uid.isNullOrBlank()){
            Toast.makeText(context, "Si è verificato un problema, accedere di nuovo e riprovare", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }
        val rv=Fragview!!.findViewById<RecyclerView>(R.id.savedRV)
        rv.layoutManager = LinearLayoutManager(context)

        PostDatabase.getPosts("saved", uid!!){postList->
            var liked = LikeRepository.getLikes()
            if (!liked.isEmpty()){
                postList.forEach{ el->
                    if (liked.containsKey(el.id)){
                        el.isLiked=true
                    }
                }
            }
            val saved = BookmarkRepository.getSaved()
            if (!saved.isEmpty()){
                postList.forEach{el->
                    if (saved.containsKey(el.id)){
                        el.isSaved=true
                    }
                }
            }

            adapter = PostCardAdapter(
                postList.toMutableList(),
                null,
                requireContext()
            )
            val handler = PostCardHandler(
                contextProvider = {requireContext()},
                adapter=adapter
            )
            adapter!!.pch=handler
            rv.adapter=adapter
        }
    }

}