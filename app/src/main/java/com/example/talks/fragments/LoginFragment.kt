package com.example.talks.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.talks.EmptyActivity
import com.example.talks.MainActivity
import com.example.talks.R
import com.example.talks.database.AccountDatabase
import com.example.talks.singleton.LastPage
import com.example.talks.singleton.UserID
import kotlinx.coroutines.launch

class LoginFragment: Fragment(R.layout.login) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loginBtn = view.findViewById<LinearLayout>(R.id.loginBtn)
        val mail = view.findViewById<EditText>(R.id.emailET)
        val password = view.findViewById<EditText>(R.id.pwET)
        val settingsBtn = view.findViewById<ImageView>(R.id.settingsBtn)
        val register = view.findViewById<LinearLayout>(R.id.register)



        loginBtn.setOnClickListener {
            loginBtn.isEnabled=false

            if (mail.text.isEmpty()){
                mail.error=getString(R.string.missingmail)
                loginBtn.isEnabled=true
            }
            else if (password.text.isEmpty()){
                password.error=getString(R.string.missingpassword)
                loginBtn.isEnabled=true
            }else{
                lifecycleScope.launch {
                    val id = AccountDatabase.login(mail.text.toString(), password.text.toString())

                    if (id==null){
                        Toast.makeText(requireContext(), getString(R.string.errLogin), Toast.LENGTH_SHORT).show()
                        loginBtn.isEnabled=true
                    }else if(id!=""){
                        UserID.setUID(id)
                        val initres = AccountDatabase.userInit(id)
                        if (!initres){
                            Toast.makeText(requireContext(), getString(R.string.errLogin), Toast.LENGTH_SHORT).show()
                        }
                        LastPage.setPage("home")
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.frame, HomePageFragment())
                            .commit()
                        (activity as MainActivity).bottombar("home")
                    }else{
                        //utente registrato ma senza account
                        val intent = Intent(requireContext(), EmptyActivity::class.java)
                            .putExtra("screen","acccreation")
                        startActivity(intent)
                    }
                }
            }
        }
        settingsBtn.setOnClickListener{
            val intent = Intent(requireContext(), EmptyActivity::class.java)
                .putExtra("screen", "sett")
            startActivity(intent)
        }
        register.setOnClickListener {
            val intent = Intent(requireContext(), EmptyActivity::class.java)
                .putExtra("screen", "register")
            startActivity(intent)
        }

    }
}