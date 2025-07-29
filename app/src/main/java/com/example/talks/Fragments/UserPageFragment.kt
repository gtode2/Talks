package com.example.talks.Fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.talks.MainActivity
import com.example.talks.R

class UserPageFragment:Fragment(R.layout.userpage) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var yourposts = view.findViewById<Button>(R.id.yourpostsBtn)
        var saved = view.findViewById<Button>(R.id.savedBtn)
        var settings = view.findViewById<Button>(R.id.settingsBtn)
        var logout = view.findViewById<Button>(R.id.logoutBtn)


        logout.setOnClickListener{
            (requireActivity() as MainActivity).logout()
        }
        settings.setOnClickListener{
           val ft = requireActivity().supportFragmentManager.beginTransaction()
           ft.replace(R.id.frame, SettingsFragment())
               .commit()
        }
    }
}