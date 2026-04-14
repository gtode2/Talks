package com.example.talks.interfaces

interface PostCard{
    fun openPost(postId:String)
    fun openUser(userId:String)
    //openuser funziona sia per utente che per tag
    fun openSource(link:String){}
    //OPZIONALE
    fun  openImage(imgId:String){}
    //OPZIONALE

    fun addLike(postId: String)
    fun savePost(postId: String)

}
interface PostCardHomepage:PostCard{

    fun openComments(postId:String)
}
interface PostCardYourPosts:PostCard{
    fun editPost(postId: String)
    fun deletePost(postId: String)
}