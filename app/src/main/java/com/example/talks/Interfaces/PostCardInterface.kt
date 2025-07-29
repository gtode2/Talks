package com.example.talks.Interfaces

interface PostCard{
    fun openPost(postId:String)
    fun openUser(userId:String)
    //openuser funziona sia per utente che per tag
    fun openLink(link:String){}
    //openLink funziona per link in testo, per fonti e per click su immagine di anteprima del link
    //OPZIONALE
    fun  openImage(imgId:String){}
    //OPZIONALE
    fun openComments(postId:String)
}
interface PostCardHomepage{
    fun addLike(postId: String)
    fun savePost(postId: String)
}
interface PostCardYourPosts{
    fun editPost(postId: String)
    fun deletePost(postId: String)
}