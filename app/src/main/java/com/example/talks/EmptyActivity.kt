package com.example.talks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.talks.fragments.PostFSFragment
import com.example.talks.fragments.YourPostsFragment
import com.example.talks.fragments.SavedPostsFragment
import com.example.talks.fragments.UserPageFragment

class EmptyActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.emptylayout)

        var id:String? = null
        val screen = intent.getStringExtra("screen")
        if (screen == "fs" || screen == "user"){
            id=intent.getStringExtra("id")
        }


        val map: Map<String, () -> Fragment> = mapOf(
            "fs" to {
                PostFSFragment().apply {
                    arguments=Bundle().apply {
                        putString("postid",id)
                    }
                }},
            "your" to {YourPostsFragment()},
            "saved" to {SavedPostsFragment()},
            "user" to {
                UserPageFragment().apply {
                    arguments=Bundle().apply {
                        putString("id",id)
                    }
                }
            }
        )

        val fragment = map[screen]?.invoke()?:throw IllegalArgumentException()

        supportFragmentManager.beginTransaction()
            .replace(R.id.emptyframe, fragment)
            .commit()
    }
}