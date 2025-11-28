package com.example.talks.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.AppSettings
import com.example.talks.PostCardHandler
import com.example.talks.R
import com.example.talks.adapters.PostCardAdapter
import com.example.talks.interfaces.PostCardHomepage

class SavedPostsFragment:Fragment(R.layout.savedposts) {
    var adapter: PostCardAdapter?=null
    private var UID:String?=null
    val settings = requireActivity().applicationContext as AppSettings
    var uid = settings.getUID()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (uid.isNullOrBlank()){
            //Toast.makeText(this, "Si è verificato un problema, accedere di nuovo e riprovare", Toast.LENGTH_SHORT).show()
            //finish()
        }
        val rv=view.findViewById<RecyclerView>(R.id.savedRV)
        rv.layoutManager = LinearLayoutManager(context)

    }

}