package com.example.talks

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.talks.Fragments.HomePageFragment
import com.example.talks.Fragments.NotificationPageFragment
import com.example.talks.Fragments.SearchPageFragment
import com.example.talks.Fragments.UserPageFragment
import com.example.talks.Fragments.UserUnloggedFragment


var act = "home"
var lgd = false
class MainActivity : AppCompatActivity() {
    lateinit var homebtn:ImageView
    lateinit var searchbtn:ImageView
    lateinit var cpstbtn:ImageView
    lateinit var accbtn:ImageView
    lateinit var ntfybtn:ImageView
    var col_act:Int?=null
    var col_nact:Int?=null




    fun btnreset(){
        when(act){
            "home"->homebtn.imageTintList=ColorStateList.valueOf(col_nact!!)
            "search"->searchbtn.imageTintList=ColorStateList.valueOf(col_nact!!)
            "cpst"->cpstbtn.imageTintList=ColorStateList.valueOf(col_nact!!)
            "acc"->accbtn.imageTintList=ColorStateList.valueOf(col_nact!!)
            "ntfy"->ntfybtn.imageTintList=ColorStateList.valueOf(col_nact!!)
        }
    }
    fun accountpage(){
        if (act!="acc"){
            btnreset()
            accbtn.imageTintList = ColorStateList.valueOf(col_act!!)
            act="acc"
            if (lgd){
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame, UserPageFragment())
                    .commit()
            }else{
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame, UserUnloggedFragment())
                    .commit()
            }
        }
    }
    fun setLgd(par:Boolean){
        lgd=par
    }
    fun logout(){
        lgd=false
        act="home"
        accountpage()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        homebtn = findViewById(R.id.homebtn)
        searchbtn = findViewById(R.id.searchbtn)
        cpstbtn = findViewById(R.id.cpstbtn)
        accbtn = findViewById(R.id.accbtn)
        ntfybtn = findViewById(R.id.ntfybtn)
        col_act = ContextCompat.getColor(this, R.color.lime)
        col_nact = ContextCompat.getColor(this, R.color.desel)
        val intent = intent
        val from = intent.getStringExtra("From")?:0
        if (from=="Login"){
            lgd=true
            act="home"
        }

        if (act=="home"){
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame, HomePageFragment())
                .commit()
        }


        homebtn.setOnClickListener{
            if (act!="home"){
                btnreset()
                homebtn.imageTintList = ColorStateList.valueOf(col_act!!)
                act="home"
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame, HomePageFragment())
                    .commit()
            }
        }
        searchbtn.setOnClickListener{
            if (act!="search"){
                btnreset()
                searchbtn.imageTintList = ColorStateList.valueOf(col_act!!)
                act="search"
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame, SearchPageFragment())
                    .commit()
            }
        }
        cpstbtn.setOnClickListener{
            if (lgd){
                val intent = Intent(this, PostCreationActivity::class.java)
                startActivity(intent)
            }
        }
        accbtn.setOnClickListener{
            accountpage()
        }
        ntfybtn.setOnClickListener{
            if (act!="ntfy"){
                btnreset()
                ntfybtn.imageTintList = ColorStateList.valueOf(col_act!!)
                act="ntfy"
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame, NotificationPageFragment())
                    .commit()
            }
        }





        //enableEdgeToEdge()

    }


}
