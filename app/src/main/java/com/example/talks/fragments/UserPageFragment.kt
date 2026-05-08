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
    private lateinit var settingsManager: SettingsManager
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
        usertag.text = userid
        val followers = view.findViewById<TextView>(R.id.upflr)
        val followed = view.findViewById<TextView>(R.id.upfld)
        val followbtn = view.findViewById<Button>(R.id.followbutton)
        val userImg = view.findViewById<ImageView>(R.id.userImg)


        if (!UserID.getUID().isNullOrBlank()){
            UID = UserID.getUID()
        }else{
            followbtn.visibility=View.GONE
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val bmp = ImageCache.get("profile${userid}")

            withContext(Dispatchers.Main) {
                if (bmp!=null){
                    userImg.setImageBitmap(bmp)
                }
                //se non esiste immagine -> lascia default
            }
        }

        var rv = view.findViewById<RecyclerView>(R.id.uprv)
        rv.layoutManager=LinearLayoutManager(context)


        lifecycleScope.launch {
            val user = UserDatabase.getUser(userid!!)
            if (user.getValue("fw").toInt()>-1) {
                followed.text = user.getValue("fd")
                followers.text = user.getValue("fw")
            }

            withContext(Dispatchers.IO){
                val postList = PostDatabase.getPosts("user", userid!!)
                withContext(Dispatchers.Main){
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


        //verifico se utente è seguito o no
        if(FollowRepository.isFollowed(userid!!)){
            followbtn.text="unfollow"
        }else{
            followbtn.text="follow"
        }


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
            }
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

    override fun onResume() {
        super.onResume()

    }
}