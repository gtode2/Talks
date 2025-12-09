package com.example.talks.fragments


import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.AppSettings
import com.example.talks.PostCardHandler
import com.example.talks.R
import com.example.talks.adapters.PostCardAdapter
import com.example.talks.data.PostData
import com.example.talks.database.PostDatabase
import com.example.talks.repository.BookmarkRepository
import com.example.talks.repository.LikeRepository


class UserPageFragment:Fragment(R.layout.userpage) {
    var userid:String?=null
    var UID:String?=null
    var adapter:PostCardAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userid = arguments?.getString("id")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usertag = view.findViewById<TextView>(R.id.uptag)
        usertag.setText(userid)

        val settings = requireActivity().applicationContext as AppSettings
        if (!settings.getUID().isNullOrBlank()){
            UID = settings.getUID()
        }

        var rv = view.findViewById<RecyclerView>(R.id.uprv)
        rv.layoutManager=LinearLayoutManager(context)

        PostDatabase.getPosts("user", userid!!){ postList->
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
                adapter = adapter,
                null,
                null
            )
            adapter!!.pch=handler

            rv.adapter=adapter
        }


    }
}