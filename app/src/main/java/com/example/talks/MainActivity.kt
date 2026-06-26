package com.example.talks

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.talks.fragments.HomePageFragment
import com.example.talks.fragments.NotificationPageFragment
import com.example.talks.fragments.SearchPageFragment
import com.example.talks.fragments.AccountPageFragment
import com.example.talks.fragments.LoginFragment
import com.example.talks.managers.SettingsManager
import com.example.talks.singleton.LastPage
import com.example.talks.singleton.UserID


class MainActivity : AppCompatActivity() {
    lateinit var homebtn:LinearLayout
    lateinit var searchbtn:LinearLayout
    lateinit var cpstbtn:LinearLayout
    lateinit var accbtn:LinearLayout
    lateinit var ntfbtn: LinearLayout

    lateinit var homeicon: ImageView
    lateinit var searchicon: ImageView
    lateinit var accicon: ImageView
    lateinit var ntficon: ImageView
    var colAct:Int?=null
    var colNact:Int?=null


    fun bottombar(p:String){
        homeicon.imageTintList=ColorStateList.valueOf(colNact!!)
        searchicon.imageTintList=ColorStateList.valueOf(colNact!!)
        accicon.imageTintList=ColorStateList.valueOf(colNact!!)
        ntficon.imageTintList=ColorStateList.valueOf(colNact!!)

        when(p){
            "home"->homeicon.imageTintList=ColorStateList.valueOf(colAct!!)
            "search"->searchicon.imageTintList=ColorStateList.valueOf(colAct!!)
            "acc"->accicon.imageTintList=ColorStateList.valueOf(colAct!!)
            "ntf"->ntficon.imageTintList=ColorStateList.valueOf(colAct!!)
        }
    }
    fun accountpage(){
        bottombar("acc")
        LastPage.setPage("acc")
        val uid = UserID.getUID()
        if (uid!=null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame, AccountPageFragment())
                .commit()
        }else{
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame, LoginFragment())
                .commit()
        }

    }
    fun logout(){
        LastPage.setPage("home")
        UserID.setUID(null)
        accountpage()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val settings = SettingsManager(this)
        settings.applyLang()
        settings.applyTheme()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)


        homebtn = findViewById(R.id.homebtn)
        searchbtn = findViewById(R.id.searchbtn)
        cpstbtn = findViewById(R.id.cpstbtn)
        accbtn = findViewById(R.id.accbtn)
        ntfbtn = findViewById(R.id.ntfbtn)
        colAct = ContextCompat.getColor(this, R.color.lime)
        colNact = ContextCompat.getColor(this, R.color.desel)

        homeicon = findViewById(R.id.homeicon)
        searchicon = findViewById(R.id.searchicon)
        accicon = findViewById(R.id.accicon)
        ntficon = findViewById(R.id.ntficon)



        //refresh bottombar
        bottombar(LastPage.getPage())


        if (savedInstanceState == null){
            val from = intent.getStringExtra("From")?:0
            if(from=="user") {

                accountpage()
            }else{
                when(LastPage.getPage()){
                    "home"->supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, HomePageFragment())
                        .commit()
                    "search"->supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, SearchPageFragment())
                        .commit()
                    "acc" -> {
                        accountpage()
                    }

                    "ntf" -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.frame, NotificationPageFragment())
                            .commit()
                    }
                }
            }
        }


        homebtn.setOnClickListener{
            if (LastPage.getPage() !="home"){
                bottombar("home")
                LastPage.setPage("home")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame, HomePageFragment())
                    .commit()
            }
        }
        searchbtn.setOnClickListener{
            if (LastPage.getPage() !="search"){
                bottombar("search")
                LastPage.setPage("search")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame, SearchPageFragment())
                    .commit()
            }
        }
        cpstbtn.setOnClickListener{
            if (UserID.getUID()!=null){
                val intent = Intent(this, EmptyActivity::class.java)
                    .putExtra("screen", "pc")
                startActivity(intent)
            }
        }
        accbtn.setOnClickListener{
            accountpage()
        }
        ntfbtn.setOnClickListener{
            if (LastPage.getPage() !="ntf"){
                bottombar("ntf")
                LastPage.setPage("ntf")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame, NotificationPageFragment())
                    .commit()
            }
        }

    }
}
