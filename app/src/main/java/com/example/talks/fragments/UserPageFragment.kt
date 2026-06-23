package com.example.talks.fragments


import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.PostCardHandler
import com.example.talks.R
import com.example.talks.adapters.UserPageAdapter
import com.example.talks.database.PostDatabase
import com.example.talks.database.UserDatabase
import com.example.talks.repository.BookmarkRepository
import com.example.talks.repository.LikeRepository
import com.example.talks.singleton.LastPost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class UserPageFragment:Fragment(R.layout.userpage) {
    var userid:String?=null
    var adapter: UserPageAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userid = arguments?.getString("id")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val frame = view.findViewById<FrameLayout>(R.id.frame)


        val usertag = view.findViewById<TextView>(R.id.userTag)
        usertag.text = userid

        val backbtn = view.findViewById<ImageView>(R.id.back)


        var rv = view.findViewById<RecyclerView>(R.id.uprv)
        rv.layoutManager=LinearLayoutManager(context)


        lifecycleScope.launch {
            val user = withContext(Dispatchers.IO){UserDatabase.getUser(userid!!)}

            if (user.followers<0) {
                val view = layoutInflater.inflate(R.layout.errorpage, frame, true)
                view.findViewById<TextView>(R.id.text).text=getString(R.string.errUserNF)
            }

            var postList = withContext(Dispatchers.IO){PostDatabase.getPosts("user", userid!!)}

            val liked = LikeRepository.getLikes()

            if (postList==null){
                postList= emptyList()
            }
            if (!liked.isEmpty()){
                postList.forEach { el->
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

            adapter = UserPageAdapter(
                postList.toMutableList(),
                null,
                requireContext(),
                user
            )
            val handler = PostCardHandler(
                requireContext(),
                adapter,
                null,
            )
            adapter!!.pch=handler

            rv.adapter=adapter

        }

        backbtn.setOnClickListener {
            requireActivity().finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val lp = LastPost.getPost()
        if (lp!=null){
            val id = lp.id
            if (id!=null){
                if (lp.liked!= LikeRepository.isLiked(id)){
                    if (lp.liked){
                        adapter?.decrLike(id)
                    }else{
                        adapter?.incrLike(id)
                    }
                }

                if (lp.saved!= BookmarkRepository.isSaved(id)){
                    if (lp.saved){
                        adapter?.unsavePost(id)
                    }else{
                        adapter?.savePost(id)
                    }
                }

                if (LastPost.getCC()!=0){
                    adapter?.commCount(id)
                }
            }
        }
    }

}