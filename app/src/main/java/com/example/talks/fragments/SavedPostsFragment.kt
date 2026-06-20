package com.example.talks.fragments

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.PostCardHandler
import com.example.talks.R
import com.example.talks.adapters.SavedPostsAdapter
import com.example.talks.database.PostDatabase
import com.example.talks.repository.BookmarkRepository
import com.example.talks.repository.LikeRepository
import com.example.talks.singleton.LastPost
import com.example.talks.singleton.UserID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SavedPostsFragment:Fragment(R.layout.savedposts) {
    var adapter: SavedPostsAdapter?=null
    private var uid:String?=null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val frame = view.findViewById<FrameLayout>(R.id.frame)

        uid = UserID.getUID()
        if (uid.isNullOrBlank()){
            Toast.makeText(context, getString(R.string.errReLog), Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }
        val rv=view.findViewById<RecyclerView>(R.id.savedRV)
        rv.layoutManager = LinearLayoutManager(context)

        val back = view.findViewById<ImageView>(R.id.backbtn)
        back.setOnClickListener {
            requireActivity().finish()
        }

        lifecycleScope.launch{
            val postList = withContext(Dispatchers.IO){PostDatabase.getPosts("saved", uid!!)}

            if (postList==null){
                val view = layoutInflater.inflate(R.layout.errorpage, frame, true)
                view.findViewById<TextView>(R.id.text).text=getString(R.string.errLoading)
            }else if(postList.isEmpty()){
                val view = layoutInflater.inflate(R.layout.errorpage, frame, true)
                view.findViewById<TextView>(R.id.text).text=getString(R.string.noposts)
            }else{
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

                adapter = SavedPostsAdapter(
                    postList.toMutableList(),
                    null,
                    requireContext()
                )
                val handler = PostCardHandler(
                    requireContext(),
                    adapter
                )
                adapter!!.pc=handler
                rv.adapter=adapter

            }
        }
    }
    override fun onResume() {
        super.onResume()
        val lp = LastPost.getPost()
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