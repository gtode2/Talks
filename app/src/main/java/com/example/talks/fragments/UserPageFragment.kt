package com.example.talks.fragments


import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.PostCardHandler
import com.example.talks.R
import com.example.talks.adapters.PostCardAdapter
import com.example.talks.data.PostData
import com.example.talks.database.PostDatabase
import com.example.talks.database.UserDatabase
import com.example.talks.managers.SettingsManager
import com.example.talks.repository.BookmarkRepository
import com.example.talks.repository.FollowedRepository
import com.example.talks.repository.LikeRepository
import com.example.talks.singleton.UserID


class UserPageFragment:Fragment(R.layout.userpage) {
    private lateinit var settingsManager: SettingsManager
    var userid:String?=null
    var UID:String?=null
    var adapter:PostCardAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userid = arguments?.getString("id")
        settingsManager = SettingsManager(requireContext())
        settingsManager.applyLang()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val usertag = view.findViewById<TextView>(R.id.uptag)
        usertag.text = userid
        val followers = view.findViewById<TextView>(R.id.upflr)
        val followed = view.findViewById<TextView>(R.id.upfld)
        val followbtn = view.findViewById<Button>(R.id.followbutton)

        if (!UserID.getUID().isNullOrBlank()){
            UID = UserID.getUID()
        }else{
            followbtn.visibility=View.GONE
        }

        var rv = view.findViewById<RecyclerView>(R.id.uprv)
        rv.layoutManager=LinearLayoutManager(context)

        UserDatabase.getUser(userid!!){ res->
            if (res.getValue("fw")>-1) {
                followed.text = res.getValue("fd").toString()
                followers.text = res.getValue("fw").toString()
            }
        }
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

        //verifico se utente è seguito o no
        if(FollowedRepository.isFollowed(userid!!)){
            followbtn.text="unfollow"
        }else{
            followbtn.text="follow"
        }
        followbtn.setOnClickListener {
            //verifica se esiste o no
            if(FollowedRepository.isFollowed(userid!!)) {
                UserDatabase.unfollow(UID!!, userid!!) { res ->
                    if (res != -1) {
                        //rimozione eseguita
                        //ridurre count
                        followers.text = (followers.text.toString().toInt() - 1).toString()
                        FollowedRepository.removeFollowed(userid!!)
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
                        FollowedRepository.addFollowed(userid!!)
                        //modifica bottone
                        followbtn.text = "unfollow"
                    }
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()

    }
}