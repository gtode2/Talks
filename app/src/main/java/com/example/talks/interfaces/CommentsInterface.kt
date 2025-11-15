package com.example.talks.interfaces

interface Comment{
    fun openUser() //implemento?
}
interface CommentYourPost:Comment{
    fun banComment()
}
interface YourComment{
    fun addComment(){}
    fun deleteComment(){}
    fun editComment(){}
}