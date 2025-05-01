package com.example.talks.Fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.talks.R

class HomePageFragment:Fragment(R.layout.homepage) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var containerlayout = view.findViewById<LinearLayout>(R.id.llayout)
        var scrollView = view.findViewById<ScrollView>(R.id.scrollv1)

        fun postAdd(i:Int, containerLayout:View){
            val newTxtV = TextView(requireContext()).apply{
                text="nuovo elemento"
                textSize=18f
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setPadding(30,16,16,16)
            }
            (containerLayout as? LinearLayout)?.addView(newTxtV)
            scrollView.post{
                scrollView.fullScroll(View.FOCUS_DOWN)
            }
        }

        var i:Int = 0
        while (i<50){
            postAdd(i, containerlayout)
            i++
        }
    }
}