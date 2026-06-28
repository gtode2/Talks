package com.example.talks.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.PostCardHandler
import com.example.talks.R
import com.example.talks.adapters.SearchAdapter
import com.example.talks.data.PostData
import com.example.talks.data.UserData
import com.example.talks.database.PostDatabase
import com.example.talks.database.UserDatabase
import com.example.talks.repository.BookmarkRepository
import com.example.talks.repository.LikeRepository
import com.example.talks.singleton.LastPost
import com.example.talks.singleton.UserID
import kotlinx.coroutines.launch



class SearchViewModel: ViewModel(){
    var ud:UserData?=null
    var posts:List<PostData>?=null
    var isLoaded:Boolean=false
}
class SearchPageFragment:Fragment(R.layout.searchpage) {
    var adapter: SearchAdapter?=null
    private lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchbtn = view.findViewById<ImageView>(R.id.searchbtn)
        val searchbar = view.findViewById<EditText>(R.id.searchstring)
        val frame = view.findViewById<FrameLayout>(R.id.frame)

        val rv = view.findViewById<RecyclerView>(R.id.searchrv)
        rv.layoutManager= LinearLayoutManager(context)

        if (viewModel.isLoaded){
            loadContent(viewModel.ud, viewModel.posts, rv, frame)
        }


        searchbtn.setOnClickListener {
            searchbtn.isEnabled=false
            frame.removeAllViews()

            val string = searchbar.text.toString().trim()
            if (string==""){
                searchbtn.isEnabled=true
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val res = UserDatabase.searchUser(string)
                if (res.err=="") {
                    Toast.makeText(requireContext(),getString(R.string.errUserSearch),Toast.LENGTH_SHORT).show()
                    viewModel.ud=null
                }else{
                    if (res.err!="n" && res.Uid!=UserID.getUID()){
                        viewModel.ud=res //utente trovato solo se != da utente loggato
                    }
                }

                viewModel.posts = PostDatabase.getPosts("search", string)

                viewModel.isLoaded=true
                loadContent(viewModel.ud,viewModel.posts, rv, frame)
                searchbtn.isEnabled=true
            }
        }

    }

    fun loadContent(ud:UserData?, posts:List<PostData>?, rv:RecyclerView, frame: FrameLayout){
        frame.removeAllViews()

        if (posts==null){
            frame.visibility=View.VISIBLE
            rv.visibility=View.GONE

            val view = layoutInflater.inflate(R.layout.errorpage, frame, true)
            view.findViewById<TextView>(R.id.text).text = getString(R.string.errLoading)
        }else if(posts.isEmpty() && ud==null){
            frame.visibility=View.VISIBLE
            rv.visibility=View.GONE

            val view = layoutInflater.inflate(R.layout.errorpage, frame, true)
            view.findViewById<TextView>(R.id.text).text = getString(R.string.emptysearch)
        }else{
            frame.visibility=View.GONE
            rv.visibility=View.VISIBLE

            val ctx = requireContext()
            val liked = LikeRepository.getLikes()
            if (!liked.isEmpty()){
                posts.forEach{ el->
                    if (liked.containsKey(el.id)){
                        el.isLiked=true
                    }
                }
            }
            val saved = BookmarkRepository.getSaved()
            if (!saved.isEmpty()){
                posts.forEach{el->
                    if (saved.containsKey(el.id)){
                        el.isSaved=true
                    }
                }
            }

            adapter = SearchAdapter(
                null,
                null,
                ctx,
                viewModel.ud
            )
            if (posts.isNotEmpty()){
                adapter?.posts =posts.toMutableList()
            }
            val handler = PostCardHandler(
                requireContext(),
                adapter)
            adapter!!.pch=handler
            rv.adapter = adapter

        }
    }

    override fun onResume() {
        super.onResume()
        val lp = LastPost.getPost()
        if (lp!=null){
            val id = lp.id
            if (id!=null){
                if (lp.liked!= LikeRepository.isLiked(id)){
                    if (lp.liked){
                        adapter?.decrLike(id)
                    }else{
                        adapter?.incrLike(id)
                    }
                }

                if (lp.saved!= BookmarkRepository.isSaved(id)){
                    if (lp.saved){
                        adapter?.unsavePost(id)
                    }else{
                        adapter?.savePost(id)
                    }
                }

                if (LastPost.getCC()!=0){
                    adapter?.commCount(id)
                }
            }
        }
    }
}