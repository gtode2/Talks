package com.example.talks

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import com.example.talks.ui.theme.TalksTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat

var act = "home"


class MainActivity : ComponentActivity() {


    private lateinit var containerLayout: LinearLayout
    private lateinit var scrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        val homebtn:ImageView = findViewById(R.id.homebtn)
        val searchbtn:ImageView = findViewById(R.id.searchbtn)
        val cpstbtn:ImageView = findViewById(R.id.cpstbtn)
        val accbtn:ImageView = findViewById(R.id.accbtn)
        val ntfybtn:ImageView = findViewById(R.id.ntfybtn)

        val col_act = ContextCompat.getColor(this, R.color.lime)
        val col_nact = ContextCompat.getColor(this, R.color.desel)
        containerLayout = findViewById(R.id.llcont)
        scrollView = findViewById(R.id.scrollv1)

        fun postAdd(i:Int){
            val newTxtV = TextView(this).apply{
                text="nuovo elemento"
                textSize=18f
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setPadding(30,16,16,16)
            }
            containerLayout.addView(newTxtV)
            scrollView.post{
                scrollView.fullScroll(View.FOCUS_DOWN)
            }
        }

        fun  btnreset(){
            when(act){
                "home"->homebtn.imageTintList=ColorStateList.valueOf(col_nact)
                "search"->searchbtn.imageTintList=ColorStateList.valueOf(col_nact)
                "cpst"->cpstbtn.imageTintList=ColorStateList.valueOf(col_nact)
                "acc"->accbtn.imageTintList=ColorStateList.valueOf(col_nact)
                "ntfy"->ntfybtn.imageTintList=ColorStateList.valueOf(col_nact)
            }
        }
        homebtn.setOnClickListener{
            if (act!="home"){
                btnreset()
                homebtn.imageTintList = ColorStateList.valueOf(col_act)
                act="home"
            }
        }
        searchbtn.setOnClickListener{
            if (act!="search"){
                btnreset()
                searchbtn.imageTintList = ColorStateList.valueOf(col_act)
                act="search"
            }
        }
        cpstbtn.setOnClickListener{
            if (act!="cpst"){
                btnreset()
                cpstbtn.imageTintList = ColorStateList.valueOf(col_act)
                act="cpst"
            }
        }
        accbtn.setOnClickListener{
            if (act!="acc"){
                btnreset()
                accbtn.imageTintList = ColorStateList.valueOf(col_act)
                act="acc"
            }
        }
        ntfybtn.setOnClickListener{
            if (act!="ntfy"){
                btnreset()
                ntfybtn.imageTintList = ColorStateList.valueOf(col_act)
                act="ntfy"
            }
        }


        var i:Int = 0
        while (i<50){
            postAdd(i)
            i++
        }

        //enableEdgeToEdge()

    }

}
