package com.example.talks.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.EmptyActivity
import com.example.talks.R
import com.example.talks.adapters.NotificationsAdapter
import com.example.talks.database.NotificationsDatabase
import com.example.talks.singleton.UserID
import kotlinx.coroutines.launch

class NotificationPageFragment:Fragment(R.layout.notificationpage) {
    var adapter: NotificationsAdapter?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val frame = view.findViewById<FrameLayout>(R.id.frame)

        if (!UserID.getUID().isNullOrBlank()){
            val rv = view.findViewById<RecyclerView>(R.id.notifrv)
            rv.layoutManager= LinearLayoutManager(context)
            lifecycleScope.launch {
                val list = NotificationsDatabase.get()


                if (list.isEmpty()){
                    val view = layoutInflater.inflate(R.layout.errorpage, frame, true)
                    view.findViewById<TextView>(R.id.text).text=getString(R.string.nonotif)
                }
                else if (list[0].err){
                    if (list[0].author=="nl"){
                        val view = layoutInflater.inflate(R.layout.errorpage, frame, true)
                        view.findViewById<TextView>(R.id.text).text=getString(R.string.notifnotlogged)
                    }else{
                        val view = layoutInflater.inflate(R.layout.errorpage, frame, true)
                        view.findViewById<TextView>(R.id.text).text=getString(R.string.errLoading)
                    }
                } else{
                    adapter=NotificationsAdapter(list.asReversed().toMutableList(), requireContext(), this@NotificationPageFragment)
                    rv.adapter=adapter
                }
            }
        }else{
            val view = layoutInflater.inflate(R.layout.errorpage, frame, true)
            view.findViewById<TextView>(R.id.text).text=getString(R.string.notifnotlogged)
        }
    }
    fun openUser(id:String){
        val intent = Intent(requireContext(), EmptyActivity::class.java)
            .putExtra("screen", "user")
            .putExtra("id", id)
        startActivity(intent)
    }
    fun openPost(id:String){
        val intent = Intent(requireContext(), EmptyActivity::class.java)
            .putExtra("screen", "fs")
            .putExtra("id", id)
        startActivity(intent)
    }
}