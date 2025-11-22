package com.example.talks.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.AppSettings
import com.example.talks.LikeRepository
import com.example.talks.adapters.PostCardAdapter
import com.example.talks.interfaces.PostCard
import com.example.talks.interfaces.PostCardHomepage
import com.example.talks.PostActivity
import com.example.talks.R
import com.example.talks.database.PostDatabase

class HomePageFragment:Fragment(R.layout.homepage), PostCardHomepage {
    var adapter:PostCardAdapter?=null
    private var UID:String?=null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val settings = requireActivity().applicationContext as AppSettings
        if (!settings.getUID().isNullOrBlank()){
            UID = settings.getUID()
        }

        var recyclerViewHomepage = view.findViewById<RecyclerView>(R.id.homepageRV)
        recyclerViewHomepage.layoutManager = LinearLayoutManager(context)
        PostDatabase.getPosts {postList->
            adapter = PostCardAdapter(postList.toMutableList(), this, requireContext())
            recyclerViewHomepage.adapter = adapter
        }

    }

    override fun openPost(postId: String) {
        Log.e("NVNC", "caricamento nuova activity")
        val intent = Intent(requireContext(), PostActivity::class.java)
        Log.e("NVNC", "intent creato")
        intent.putExtra("id", postId)
        startActivity(intent)

    }

    override fun openComments(postId: String) {

    }

    override fun openUser(userId: String) {

    }

    override fun addLike(postId: String) {
        if (!UID.isNullOrBlank()){
            LikeRepository.addLike(UID!!,postId){res->
                //0 = aggiunta eseguita
                //1 = già aggiunto ma in altra istanza - segna come aggiunto
                //2 = già presente - rimosso
                //-1= errore


                if (res==0){
                    Toast.makeText(context, "incremento like", Toast.LENGTH_SHORT).show()
                    adapter!!.incrLike(postId)
                }else if (res==-1){
                    //errore
                    Toast.makeText(context, "si è verificato un errore", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun savePost(postId: String) {

    }
}