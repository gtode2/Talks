package com.example.talks

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PostActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.postfullscreen)

        val postId = intent.getStringExtra("id")

        val userTag = findViewById<TextView>(R.id.userTag)
        val postText = findViewById<TextView>(R.id.postText)
        val likeCtr = findViewById<TextView>(R.id.likeCtr)



        userTag.text =postId


    }
}