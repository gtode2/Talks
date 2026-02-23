package com.example.talks.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.R
import com.example.talks.adapters.NotificationsAdapter
import com.example.talks.database.NotificationsDatabase
import com.example.talks.singleton.UserID
import kotlinx.coroutines.launch

class NotificationPageFragment:Fragment(R.layout.notificationpage) {
    //carica notifiche da firebase
    var adapter: NotificationsAdapter?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!UserID.getUID().isNullOrBlank()){
            val rv = view.findViewById<RecyclerView>(R.id.notifrv)
            rv.layoutManager= LinearLayoutManager(context)
            lifecycleScope.launch {
                val list = NotificationsDatabase.get()
                Log.e("AAA", list.toString())

                adapter=NotificationsAdapter(list.toMutableList())
                rv.adapter=adapter
            }
        }else{
            //mostrare messaggio errore
        }
    }
    //crea rv
    //aggiorna rv

}