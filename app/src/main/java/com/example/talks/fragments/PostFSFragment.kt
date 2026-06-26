package com.example.talks.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.PostCardHandler
import com.example.talks.R
import com.example.talks.adapters.PostAdapter
import com.example.talks.data.CommentData
import com.example.talks.data.PostData
import com.example.talks.database.CommentsDatabase
import com.example.talks.database.PostDatabase
import com.example.talks.repository.BookmarkRepository
import com.example.talks.repository.LikeRepository
import com.example.talks.singleton.UserID
import kotlinx.coroutines.launch

class PostFSFragment:Fragment(R.layout.postfullscreen) {

    var postId:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postId = arguments?.getString("postid")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uid = UserID.getUID()
        val addCombutton = view.findViewById<ImageView>(R.id.sendcommbtn)
        val addCom = view.findViewById<View>(R.id.addcomment)
        val addComTxt = view.findViewById<EditText>(R.id.textcomment)
        val rvPost = view.findViewById<RecyclerView>(R.id.postrv)

        val frame = view.findViewById<FrameLayout>(R.id.frame)

        if (postId.isNullOrBlank()){
            val view = layoutInflater.inflate(R.layout.errorpage, frame, true)
            view.findViewById<TextView>(R.id.text).text=getString(R.string.errLoading)
            val btn = view.findViewById<Button>(R.id.btn)
            btn.visibility=View.VISIBLE
            btn.text=getString(R.string.back)
            btn.backgroundTintList = ColorStateList.valueOf(getColor(requireContext(),R.color.lime))
            btn.setOnClickListener {
                requireActivity().finish()
            }
        }else{
            var adapter:PostAdapter?=null

            if (uid.isNullOrBlank()){
                addCom.visibility=View.GONE
            }

            rvPost.layoutManager = LinearLayoutManager(requireContext())

            var post: PostData?=null

            lifecycleScope.launch{
                val postList = PostDatabase.getPost(postId!!)

                if (postList==null || postList.isEmpty()){
                    val view = layoutInflater.inflate(R.layout.errorpage, frame, true)
                    view.findViewById<TextView>(R.id.text).text=getString(R.string.errLoading)
                    val btn = view.findViewById<Button>(R.id.btn)
                    btn.visibility=View.VISIBLE
                    btn.text=getString(R.string.back)
                    btn.backgroundTintList = ColorStateList.valueOf(getColor(requireContext(),R.color.lime))
                    btn.setOnClickListener {
                        requireActivity().finish()
                    }
                }else{
                    post = postList[0]

                    var comments = CommentsDatabase.getComments(postId!!)
                    if (comments==null){
                        Toast.makeText(requireContext(), getString(R.string.errcommloading), Toast.LENGTH_SHORT).show()
                        comments = mutableListOf()
                    }

                    val liked = LikeRepository.getLikes()
                    if (liked.isNotEmpty()){
                        if (liked.containsKey(post.id)){
                            post.isLiked=true
                        }
                    }
                    val saved = BookmarkRepository.getSaved()
                    if (saved.isNotEmpty()){
                        if (saved.containsKey(post.id)){
                            post.isSaved=true
                        }
                    }
                    adapter = PostAdapter(
                        post,
                        comments,
                        null,
                        requireContext(),
                    )

                    val handler = PostCardHandler(
                        requireContext(),
                        adapter
                    )

                    adapter.pch = handler
                    rvPost.adapter = adapter
                }

            }
            addCombutton.setOnClickListener {
                val commenttext = addComTxt.text.toString()
                if (commenttext.isNotBlank()){
                    lifecycleScope.launch {
                        val res = CommentsDatabase.addComment(commenttext, postId!!, post!!.uid)
                        if (res!=-1){
                            addComTxt.text.clear()
                            adapter!!.addComment(commenttext, UserID.getUID()!!)
                            if (res==-2){
                                Toast.makeText(requireContext(), getString(R.string.errNotif), Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(requireContext(), getString(R.string.errComm), Toast.LENGTH_SHORT).show()
                }
            }
        }





    }
}