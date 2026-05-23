package com.example.talks.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.EmptyActivity
import com.example.talks.PostCardHandler
import com.example.talks.R
import com.example.talks.adapters.PostCardAdapter
import com.example.talks.adapters.PostCardSearchAdapter
import com.example.talks.data.UserData
import com.example.talks.database.PostDatabase
import com.example.talks.database.UserDatabase
import com.example.talks.repository.BookmarkRepository
import com.example.talks.repository.LikeRepository
import com.example.talks.singleton.UserID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchPageFragment:Fragment(R.layout.searchpage) {
    var adapter: PostCardSearchAdapter?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchbtn = view.findViewById<ImageView>(R.id.searchbtn)
        val searchbar = view.findViewById<EditText>(R.id.searchstring)

        val rv = view.findViewById<RecyclerView>(R.id.searchrv)


        searchbtn.setOnClickListener {
            val string = searchbar.text.toString()
            rv.layoutManager= LinearLayoutManager(context)
            var ud: UserData?=null


            lifecycleScope.launch {
                val res = UserDatabase.searchUser(string)
                if (res.followers!=-1 && res.Uid!=UserID.getUID()){
                    //se uid = utente loggato -> ignora
                    ud=res
                }

                val postList = withContext(Dispatchers.IO){PostDatabase.getPosts("search", string)}

                val ctx = requireContext()
                //racchiudere in handler?
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

                adapter = PostCardSearchAdapter(
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