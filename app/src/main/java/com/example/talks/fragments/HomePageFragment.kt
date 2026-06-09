package com.example.talks.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.EmptyActivity
import com.example.talks.repository.LikeRepository
import com.example.talks.adapters.PostCardAdapter
import com.example.talks.PostCardHandler
import com.example.talks.R
import com.example.talks.database.PostDatabase
import com.example.talks.repository.BookmarkRepository
import com.example.talks.singleton.LastPost
import com.example.talks.singleton.UserID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomePageFragment:Fragment(R.layout.homepage) {
    var adapter:PostCardAdapter?=null
    private var UID:String?=null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val frame = view.findViewById<FrameLayout>(R.id.frame)
        UID = UserID.getUID()


        var recyclerViewHomepage = view.findViewById<RecyclerView>(R.id.homepageRV)
        recyclerViewHomepage.layoutManager = LinearLayoutManager(context)
        lifecycleScope.launch{
            val postList = withContext(Dispatchers.IO){PostDatabase.getPosts()}

            //null impossibile per PostDatabase(all)
            if (postList!!.isNotEmpty()){
                val ctx = requireContext()
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
                    ctx,
                )
                val handler = PostCardHandler(
                    contextProvider = {requireContext()},
                    adapter=adapter,
                    null,
                    openUser = {userid->openUser(userid)}
                )
                adapter!!.pc=handler

                recyclerViewHomepage.adapter = adapter
            }else{
                val view = layoutInflater.inflate(R.layout.errorpage, frame, true)
                view.findViewById<TextView>(R.id.text).text = getString(R.string.emptyhomepage)
            }




        }
    }
    override fun onResume() {
        super.onResume()
        var lp = LastPost.getPost()
        if (lp.id!="-1"){
            //verifica elemento
            if (lp.liked!= LikeRepository.isLiked(lp.id)){
                //like prec != like attuale
                //se precedente è liked -> attuale no
                if (lp.liked){
                    adapter?.decrLike(lp.id)
                }else{
                    adapter?.incrLike(lp.id)
                }
            }

            if (lp.saved!= BookmarkRepository.isSaved(lp.id)){
                //save prec != save attuale
                //se precedente è saved -> attuale no
                if (lp.saved){
                    adapter?.unsavePost(lp.id)
                }else{
                    adapter?.savePost(lp.id)
                }
            }

            if (LastPost.getCC()!=0){
                //eseguo update
                adapter?.commCount(lp.id)
            }
        }
    }


    fun openUser(userId:String){
        val intent = Intent(requireContext(), EmptyActivity::class.java)
            .putExtra("screen","user")
            .putExtra("id",userId)
        startActivity(intent)
    }
}