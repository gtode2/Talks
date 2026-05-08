package com.example.talks

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
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
        when(LastPage.getPage()){
            "home"->homebtn.imageTintList=ColorStateList.valueOf(col_nact!!)
            "search"->searchbtn.imageTintList=ColorStateList.valueOf(col_nact!!)
            "cpst"->cpstbtn.imageTintList=ColorStateList.valueOf(col_nact!!)
            "acc"->accbtn.imageTintList=ColorStateList.valueOf(col_nact!!)
            "ntfy"->ntfybtn.imageTintList=ColorStateList.valueOf(col_nact!!)
        }
    }
    fun accountpage(){
        val act = LastPage.getPage()
        if (act!="acc"){
            btnreset()
            accbtn.imageTintList = ColorStateList.valueOf(col_act!!)
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
        ntfybtn = findViewById(R.id.ntfybtn)
        col_act = ContextCompat.getColor(this, R.color.lime)
        col_nact = ContextCompat.getColor(this, R.color.desel)

        val intent = intent

        val from = intent.getStringExtra("From")?:0
        if (from=="user"){
            //caricamento fragment utente
            LastPage.setPage("sett")
            accountpage()
        }

        if (from=="sett"){
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame, com.example.talks.fragments.SettingsFragment())
                .commit()
        }


        if (LastPage.getPage() =="home"){
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame, HomePageFragment())
                .commit()
        }


        homebtn.setOnClickListener{
            if (LastPage.getPage() !="home"){
                btnreset()
                homebtn.imageTintList = ColorStateList.valueOf(col_act!!)
                LastPage.setPage("home")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame, HomePageFragment())
                    .commit()
            }
        }
        searchbtn.setOnClickListener{
            if (LastPage.getPage() !="search"){
                btnreset()
                searchbtn.imageTintList = ColorStateList.valueOf(col_act!!)
                LastPage.setPage("search")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame, SearchPageFragment())
                    .commit()
            }
        }
        cpstbtn.setOnClickListener{
            if (UserID.getUID()!=null){
                val intent = Intent(this, PostCreationActivity::class.java)
                startActivity(intent)
            }
        }
        accbtn.setOnClickListener{
            accountpage()
        }
        ntfybtn.setOnClickListener{
            if (LastPage.getPage() !="ntfy"){
                btnreset()
                ntfybtn.imageTintList = ColorStateList.valueOf(col_act!!)
                LastPage.setPage("ntfy")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame, NotificationPageFragment())
                    .commit()
            }
        }

        //enableEdgeToEdge()

        //RIMUOVERE LGD -> SPOSTARE TUTTO IN UserID.getUID
    }

    fun setBottomBar(page:String){
        btnreset()
        LastPage.setPage(page)
        when(page){
            "home"->homebtn.imageTintList=ColorStateList.valueOf(col_act!!)
            "search"->searchbtn.imageTintList=ColorStateList.valueOf(col_act!!)
            "cpst"->cpstbtn.imageTintList=ColorStateList.valueOf(col_act!!)
            "acc"->accbtn.imageTintList=ColorStateList.valueOf(col_act!!)
            "ntfy"->ntfybtn.imageTintList=ColorStateList.valueOf(col_act!!)
        }

    }
}
