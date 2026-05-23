package com.example.talks.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.PostCardHandler
import com.example.talks.R
import com.example.talks.adapters.YourPostCardAdapter
import com.example.talks.data.CommentData
import com.example.talks.database.CommentsDatabase
import com.example.talks.database.PostDatabase
import com.example.talks.singleton.UserID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class YourPostsFragment:Fragment(R.layout.yourposts) {
    var adapter: YourPostCardAdapter?=null
    private var uid:String?=null
    var Fragview:View?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Fragview=view
        init()
    }


    fun init(){
        uid = UserID.getUID()
        if (uid.isNullOrBlank()){
            Toast.makeText(context, "Si è verificato un problema, accedere di nuovo e riprovare", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }
        val rv = Fragview!!.findViewById<RecyclerView>(R.id.yourRV)
        rv.layoutManager = LinearLayoutManager(context)

        val back = Fragview!!.findViewById<ImageView>(R.id.backbtn)
        back.setOnClickListener {
            requireActivity().finish()
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val postList = PostDatabase.getPosts("user", uid!!)
            withContext(Dispatchers.Main){
                Toast.makeText(context, "${postList.size}", Toast.LENGTH_SHORT).show()
                adapter = YourPostCardAdapter(
                    postList.toMutableList(),
                    null,
                    requireContext()
                )
                val handler = PostCardHandler(
                    contextProvider = {requireContext()},
                    adapter=adapter,
                    openEdit = {postId->editPost(postId)}
                )
                adapter!!.pc=handler
                rv.adapter=adapter
            }
        }
    }
    fun editPost(postId:String){
        parentFragmentManager.beginTransaction()
            .replace(R.id.emptyframe, EditPostFragment().apply {
                arguments=Bundle().apply {
                    putString("id", postId)
                }
            })
            .addToBackStack(null)
            .commit()
    }



}