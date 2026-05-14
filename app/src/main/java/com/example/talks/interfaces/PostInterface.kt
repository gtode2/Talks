package com.example.talks.interfaces

interface PostCard{
    fun openPost(postId:String)
    fun openUser(userId:String)
    //openuser funziona sia per utente che per tag
    fun openSource(link:String){}
    //OPZIONALE

    fun addLike(postId: String)
    fun savePost(postId: String)
    fun editPost(postId: String)
    fun deletePost(postId: String)

}