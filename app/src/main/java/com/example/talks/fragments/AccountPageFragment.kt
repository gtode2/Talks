package com.example.talks.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.talks.EmptyActivity
import com.example.talks.MainActivity
import com.example.talks.R

class AccountPageFragment:Fragment(R.layout.userpage_lgd) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var yourposts = view.findViewById<ConstraintLayout>(R.id.yourPostsBtn)
        var saved = view.findViewById<ConstraintLayout>(R.id.savedBtn)
        var settings = view.findViewById<ConstraintLayout>(R.id.settingsBtn)
        var logout = view.findViewById<ConstraintLayout>(R.id.logoutBtn)


        logout.setOnClickListener{
            (requireActivity() as MainActivity).logout()
        }

        settings.setOnClickListener{
           val ft = requireActivity().supportFragmentManager.beginTransaction()
           ft.replace(R.id.frame, SettingsFragment())
               .commit()
        }

        saved.setOnClickListener{
            val intent = Intent(requireContext(), EmptyActivity::class.java)
                .putExtra("screen", "saved")
            startActivity(intent)
        }
        yourposts.setOnClickListener{
            val intent = Intent(requireContext(),EmptyActivity::class.java)
                .putExtra("screen","your")
            startActivity(intent)
        }
    }
}