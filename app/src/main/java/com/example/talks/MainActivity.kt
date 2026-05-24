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


class MainActivity : AppCompatActivity() {
    lateinit var homebtn:ImageView
    lateinit var searchbtn:ImageView
    lateinit var cpstbtn:ImageView
    lateinit var accbtn:ImageView
    lateinit var ntfbtn:ImageView
    var col_act:Int?=null
    var col_nact:Int?=null


    fun bottombar(p:String){
        homebtn.imageTintList=ColorStateList.valueOf(col_nact!!)
        searchbtn.imageTintList=ColorStateList.valueOf(col_nact!!)
        cpstbtn.imageTintList=ColorStateList.valueOf(col_nact!!)
        accbtn.imageTintList=ColorStateList.valueOf(col_nact!!)
        ntfbtn.imageTintList=ColorStateList.valueOf(col_nact!!)

        when(p){
            "home"->homebtn.imageTintList=ColorStateList.valueOf(col_act!!)
            "search"->searchbtn.imageTintList=ColorStateList.valueOf(col_act!!)
            "cpst"->cpstbtn.imageTintList=ColorStateList.valueOf(col_act!!)
            "acc"->accbtn.imageTintList=ColorStateList.valueOf(col_act!!)
            "ntf"->ntfbtn.imageTintList=ColorStateList.valueOf(col_act!!)
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
        Log.d("DEBUG", "onCreate")
        Log.d("DEBUG", "savedInstanceState = $savedInstanceState")
        Log.d("DEBUG", "LastPage = ${LastPage.getPage()}")
        Log.d("DEBUG", "From = ${intent.getStringExtra("From")}")

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
        col_act = ContextCompat.getColor(this, R.color.lime)
        col_nact = ContextCompat.getColor(this, R.color.desel)


        //refresh bottombar
        bottombar(LastPage.getPage())


        if (savedInstanceState == null){
            //activity appena creata
            val from = intent.getStringExtra("From")?:0
            when(from){
                "user"->{
                    accountpage()
                }
                "sett"->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, com.example.talks.fragments.SettingsFragment())
                        .commit()
                }
                else->{
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
        }
        Log.d("DEBUG", "FRAGMENT IN FRAME = ${supportFragmentManager.findFragmentById(R.id.frame)}")


        homebtn.setOnClickListener{
            if (LastPage.getPage() !="home"){
                bottombar("home")
                //homebtn.imageTintList = ColorStateList.valueOf(col_act!!)
                LastPage.setPage("home")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame, HomePageFragment())
                    .commit()
            }
        }
        searchbtn.setOnClickListener{
            if (LastPage.getPage() !="search"){
                bottombar("search")
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
        ntfbtn.setOnClickListener{
            if (LastPage.getPage() !="ntf"){
                bottombar("ntf")
                ntfbtn.imageTintList = ColorStateList.valueOf(col_act!!)
                LastPage.setPage("ntf")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame, NotificationPageFragment())
                    .commit()
            }
        }

        //enableEdgeToEdge()

    }


}
