package com.example.talks.interfaces

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
interface PostCardHomepage:PostCard{
    fun addLike(postId: String)
    fun savePost(postId: String)
}
interface PostCardYourPosts:PostCard{
    fun editPost(postId: String)
    fun deletePost(postId: String)
}