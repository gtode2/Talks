package com.example.talks.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.talks.R
import com.example.talks.database.AccountDatabase
import com.example.talks.singleton.UserID
import kotlinx.coroutines.launch

class LoginFragment: Fragment(R.layout.login) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var loginBtn = view.findViewById<Button>(R.id.loginBtn)
        var mail = view.findViewById<EditText>(R.id.emailET)
        var password = view.findViewById<EditText>(R.id.pwET)
        var settingsBtn = view.findViewById<ImageView>(R.id.settingsBtn)


        loginBtn.setOnClickListener {
            if (mail.text.isEmpty()){
                mail.error=getString(R.string.missingmail)
            }
            else if (password.text.isEmpty()){
                password.error=getString(R.string.missingpassword)
            }else{
                lifecycleScope.launch {
                    val id = AccountDatabase.login(mail.text.toString(), password.text.toString())
                    //controllo id
                    UserID.setUID(id)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.frame, HomePageFragment())
                        .commit()
                }
            }
        }
        settingsBtn.setOnClickListener{
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame, SettingsFragment())
                .commit()
        }

    }
}