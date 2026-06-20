package com.example.talks.singleton

import com.example.talks.data.PrPostData
import com.example.talks.repository.BookmarkRepository
import com.example.talks.repository.LikeRepository

object LastPost {
    private var lastPost: String? = null

    private var liked: Boolean=false
    private var saved: Boolean=false
    private var commentCount:Int=0
    fun addPost(id:String){
        lastPost=id
        saved=BookmarkRepository.isSaved(id)
        liked= LikeRepository.isLiked(id)
    }

    fun getPost(): PrPostData{
        //gestire usage -> prima =-1, adesso = null
        return PrPostData(lastPost!!, liked, saved)
    }

    fun incrCC(){
        commentCount++
    }
    fun getCC():Int{
        val cc = commentCount
        commentCount=0
        return cc
    }
}