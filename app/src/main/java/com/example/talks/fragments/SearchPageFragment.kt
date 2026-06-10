package com.example.talks.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.EmptyActivity
import com.example.talks.PostCardHandler
import com.example.talks.R
import com.example.talks.adapters.SearchAdapter
import com.example.talks.data.UserData
import com.example.talks.database.PostDatabase
import com.example.talks.database.UserDatabase
import com.example.talks.repository.BookmarkRepository
import com.example.talks.repository.LikeRepository
import com.example.talks.singleton.LastPost
import com.example.talks.singleton.UserID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchPageFragment:Fragment(R.layout.searchpage) {
    var adapter: SearchAdapter?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchbtn = view.findViewById<ImageView>(R.id.searchbtn)
        val searchbar = view.findViewById<EditText>(R.id.searchstring)
        val frame = view.findViewById<FrameLayout>(R.id.frame)

        val rv = view.findViewById<RecyclerView>(R.id.searchrv)


        searchbtn.setOnClickListener {
            searchbtn.isEnabled=false

            val string = searchbar.text.toString().trim()
            if (string==""){
                searchbtn.isEnabled=true
                return@setOnClickListener
            }
            rv.layoutManager= LinearLayoutManager(context)
            var ud: UserData?=null


            lifecycleScope.launch {
                val res = withContext(Dispatchers.IO){UserDatabase.searchUser(string)}
                if (res!=null){
                    if (res.followers!=-1 && res.Uid!=UserID.getUID()){
                        ud=res
                    }
                }else{
                    Toast.makeText(requireContext(), getString(R.string.errUserSearch), Toast.LENGTH_SHORT).show()
                }

                val postList = withContext(Dispatchers.IO){PostDatabase.getPosts("search", string)}
                if (postList==null){
                    frame.visibility=View.VISIBLE
                    rv.visibility=View.GONE

                    val view = layoutInflater.inflate(R.layout.errorpage, frame, true)
                    view.findViewById<TextView>(R.id.text).text = getString(R.string.errLoading)
                    searchbtn.isEnabled=true
                }else if (postList.isEmpty() && ud==null){
                    frame.visibility=View.VISIBLE
                    rv.visibility=View.GONE

                    val view = layoutInflater.inflate(R.layout.errorpage, frame, true)
                    view.findViewById<TextView>(R.id.text).text = getString(R.string.emptysearch)
                    searchbtn.isEnabled=true
                }else{
                    frame.visibility=View.GONE
                    rv.visibility=View.VISIBLE


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

                    adapter = SearchAdapter(
                        null,
                        null,
                        ctx,
                        ud
                    )
                    if (postList.isNotEmpty()){
                        adapter?.posts =postList.toMutableList()
                    }
                    val handler = PostCardHandler(
                        contextProvider = {requireContext()},
                        adapter=adapter,
                        null,
                        openUser = {userid->openUser(userid)}
                    )
                    adapter!!.pch=handler
                    rv.adapter = adapter
                    searchbtn.isEnabled=true
                }




            }
        }

    }
    fun openUser(userId:String){
        val intent = Intent(requireContext(), EmptyActivity::class.java)
            .putExtra("screen","user")
            .putExtra("id",userId)
        startActivity(intent)
    }
    override fun onResume() {
        super.onResume()
        var lp = LastPost.getPost()
        if (lp.id!="-1"){
            if (lp.liked!= LikeRepository.isLiked(lp.id)){
                if (lp.liked){
                    adapter?.decrLike(lp.id)
                }else{
                    adapter?.incrLike(lp.id)
                }
            }

            if (lp.saved!= BookmarkRepository.isSaved(lp.id)){
                if (lp.saved){
                    adapter?.unsavePost(lp.id)
                }else{
                    adapter?.savePost(lp.id)
                }
            }

            if (LastPost.getCC()!=0){
                adapter?.commCount(lp.id)
            }
        }
    }
}