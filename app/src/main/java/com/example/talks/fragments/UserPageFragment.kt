package com.example.talks.fragments


import android.os.Bundle
import android.view.View
import android.widget.Button
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

        val usertag = view.findViewById<TextView>(R.id.userTag)
        usertag.text = userid

        val backbtn = view.findViewById<ImageView>(R.id.back)


        var rv = view.findViewById<RecyclerView>(R.id.uprv)
        rv.layoutManager=LinearLayoutManager(context)


        lifecycleScope.launch {
            val user = withContext(Dispatchers.IO){UserDatabase.getUser(userid!!)}

            if (user.followers<0) {
               //gestione errore -> inesistente o errore di caricamento
            }

            val postList = withContext(Dispatchers.IO){PostDatabase.getPosts("user", userid!!)}

            val liked = LikeRepository.getLikes()
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


        //verifico se utente è seguito o no

        /*
        if(FollowRepository.isFollowed(userid!!)){
            followbtn.text="unfollow"
        }else{
            followbtn.text="follow"
        }*/


        /*
        followbtn.setOnClickListener {
            lifecycleScope.launch {
                val res = FollowRepository.addFollow(userid!!)
                //gestire bottone
                if (res==0){
                    followbtn.text = "followed"
                    followers.text = (followers.text.toString().toInt() + 1).toString()
                    //NON CORRETTO -> SE ERA FOLLOWED -> NON MODIFICO
                }else if(res==1){
                    followbtn.text = "follow"
                    followers.text = (followers.text.toString().toInt() - 1).toString()
                    //NON CORRETTO -> SE ERA UNFOLLOWED -> NON MODIFICO
                }
            }*/
            /*
            if(FollowRepository.isFollowed(userid!!)) {
                UserDatabase.unfollow(UID!!, userid!!) { res ->
                    if (res != -1) {
                        //rimozione eseguita
                        //ridurre count
                        followers.text = (followers.text.toString().toInt() - 1).toString()
                        FollowRepository.removeFollowed(userid!!)
                        //modifica bottone
                        followbtn.text = "follow"
                    }
                }
            }else{
                UserDatabase.follow(UID!!, userid!!) { res ->
                    if (res != -1) {
                        //aggiunta eseguita
                        //aumentare count
                        followers.text = (followers.text.toString().toInt() + 1).toString()
                        FollowRepository.addFollowed(userid!!)
                        //modifica bottone
                        followbtn.text = "unfollow"
                    }
                }
            }}*/
        }

}