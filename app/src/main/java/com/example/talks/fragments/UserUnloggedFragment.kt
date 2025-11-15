package com.example.talks.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.talks.LoginActivity
import com.example.talks.R
import com.example.talks.RegisterActivity

class UserUnloggedFragment:Fragment(R.layout.userpage_unlgd) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var loginBtn = view.findViewById<Button>(R.id.loginBtn)
        var registerBtn = view.findViewById<Button>(R.id.registerBtn)
        loginBtn.setOnClickListener{
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
        registerBtn.setOnClickListener{
            val intent = Intent(requireContext(), RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}