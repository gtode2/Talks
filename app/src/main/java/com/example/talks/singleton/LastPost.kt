package com.example.talks.singleton

import com.example.talks.data.PrPost
import com.example.talks.repository.BookmarkRepository
import com.example.talks.repository.LikeRepository

object LastPost {
    private var lastPost: String? = null

    //liked & saved - status in adapter

    private var liked: Boolean=false
    private var saved: Boolean=false
    fun addPost(id:String){
        lastPost=id
        saved=BookmarkRepository.isSaved(id)
        liked= LikeRepository.isLiked(id)
    }

    fun getPost(): PrPost{
        if (lastPost!=null){
            return PrPost(lastPost!!, liked, saved)
        }else{
            return PrPost("-1", false, false)
        }
    }
}