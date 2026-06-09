package com.example.talks.fragments


import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.PostCardHandler
import com.example.talks.R
import com.example.talks.adapters.PostCardAdapter
import com.example.talks.adapters.UserPageAdapter
import com.example.talks.database.PostDatabase
import com.example.talks.database.UserDatabase
import com.example.talks.managers.SettingsManager
import com.example.talks.repository.BookmarkRepository
import com.example.talks.repository.FollowRepository
import com.example.talks.repository.LikeRepository
import com.example.talks.singleton.ImageCache
import com.example.talks.singleton.LastPost
import com.example.talks.singleton.UserID
import kotlinx.coroutines.CoroutineScope
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
                contextProvider = {requireContext()},
                adapter = adapter,
                null,
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
        var lp = LastPost.getPost()
        if (lp.id != "-1") {
            //verifica elemento
            if (lp.liked != LikeRepository.isLiked(lp.id)) {
                //like prec != like attuale
                //se precedente è liked -> attuale no
                if (lp.liked) {
                    adapter?.decrLike(lp.id)
                } else {
                    adapter?.incrLike(lp.id)
                }
            }

            if (lp.saved != BookmarkRepository.isSaved(lp.id)) {
                //save prec != save attuale
                //se precedente è saved -> attuale no
                if (lp.saved) {
                    adapter?.unsavePost(lp.id)
                } else {
                    adapter?.savePost(lp.id)
                }
            }

            if (LastPost.getCC() != 0) {
                //eseguo update
                adapter?.commCount(lp.id)
            }
        }
    }

}