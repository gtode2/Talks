package com.example.talks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.talks.fragments.AccountCreationFragment
import com.example.talks.fragments.PostFSFragment
import com.example.talks.fragments.ProfilePictureSelectFragment
import com.example.talks.fragments.RegisterFragment
import com.example.talks.fragments.YourPostsFragment
import com.example.talks.fragments.SavedPostsFragment
import com.example.talks.fragments.UserPageFragment

class EmptyActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.emptylayout)

        if (savedInstanceState==null){
            val screen = intent.getStringExtra("screen")
            val id = intent.getStringExtra("id")
            val imgchg = intent.getBooleanExtra("prpicChange", false)

            openScreen(screen?: throw java.lang.IllegalArgumentException(), imgchg, id)
        }
    }
    fun openScreen(screen:String, imgchg:Boolean, id:String?=null){
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
            },
            "register" to { RegisterFragment() },
            "pps" to {
                ProfilePictureSelectFragment().apply {
                    arguments= Bundle().apply {
                        putBoolean("change", imgchg)
                    }
                }
            },
            "acccreation" to { AccountCreationFragment().apply{
                arguments= Bundle().apply {
                    putString("uid",id)
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