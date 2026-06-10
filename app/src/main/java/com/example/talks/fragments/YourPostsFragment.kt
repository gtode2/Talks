package com.example.talks.fragments

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.PostCardHandler
import com.example.talks.R
import com.example.talks.adapters.YourPostCardAdapter
import com.example.talks.database.PostDatabase
import com.example.talks.singleton.UserID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class YourPostsFragment:Fragment(R.layout.yourposts) {
    var adapter: YourPostCardAdapter?=null
    private var uid:String?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val frame = view.findViewById<FrameLayout>(R.id.frame)
        uid = UserID.getUID()
        if (uid.isNullOrBlank()){
            Toast.makeText(context, getString(R.string.errReLog), Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }
        val rv = view.findViewById<RecyclerView>(R.id.yourRV)
        rv.layoutManager = LinearLayoutManager(context)

        val back = view.findViewById<ImageView>(R.id.backbtn)
        back.setOnClickListener {
            requireActivity().finish()
        }

        lifecycleScope.launch{
            val postList = withContext(Dispatchers.IO){PostDatabase.getPosts("user", uid!!)}
            if (postList==null){
                val view = layoutInflater.inflate(R.layout.errorpage, frame, true)
                view.findViewById<TextView>(R.id.text).text=getString(R.string.error)

            }else if(postList.isEmpty()){
                val view = layoutInflater.inflate(R.layout.errorpage, frame, true)
                view.findViewById<TextView>(R.id.text).text=getString(R.string.noYourPosts)
            }else{
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