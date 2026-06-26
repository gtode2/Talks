package com.example.talks.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.PostCardHandler
import com.example.talks.R
import com.example.talks.data.PostData
import com.example.talks.interfaces.PostHandlerInterface

class YourPostCardAdapter(
    private val posts:MutableList<PostData>,
    var pc: PostCardHandler?,
    private val context: Context,
):RecyclerView.Adapter<PostViewHolder>(), PostHandlerInterface{

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position], true)
    }
    override fun getItemCount(): Int = posts.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.postcardyour, parent, false)
        return PostViewHolder(view, context, pc)
    }

    override fun deletePost(postId: String) {
        val index = posts.indexOfFirst { it.id==postId }
        posts.removeIf { it.id===postId }
        notifyItemRemoved(index)
    }
}
