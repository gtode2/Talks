package com.example.talks.Fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.talks.R

class SettingsFragment:Fragment(R.layout.settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var back = view.findViewById<Button>(R.id.settcancbtn)
        back.setOnClickListener{
            val ft = requireActivity().supportFragmentManager.beginTransaction()
            ft.replace(R.id.frame, UserPageFragment())
                .commit()
        }
    }
}