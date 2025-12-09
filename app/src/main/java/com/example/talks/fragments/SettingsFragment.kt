package com.example.talks.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button

import androidx.fragment.app.Fragment
import com.example.talks.R

class SettingsFragment:Fragment(R.layout.settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var back = view.findViewById<Button>(R.id.settcancbtn)
        back.setOnClickListener{
            val ft = requireActivity().supportFragmentManager.beginTransaction()
            ft.replace(R.id.frame, AccountPageFragment())
                .commit()
        }
    }
}