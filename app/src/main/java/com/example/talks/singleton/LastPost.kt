package com.example.talks.singleton

import com.example.talks.data.PrPostData
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

    fun getPost(): PrPostData{
        if (lastPost!=null){
            return PrPostData(lastPost!!, liked, saved)
        }else{
            return PrPostData("-1", false, false)
        }
    }
}