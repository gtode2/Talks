package com.example.talks.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.example.talks.R
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.talks.EmptyActivity
import com.example.talks.database.AccountDatabase
import kotlinx.coroutines.launch


class RegisterFragment:Fragment(R.layout.register) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val back = view.findViewById<ImageView>(R.id.backbtn)
        val mail = view.findViewById<EditText>(R.id.email)
        val pw = view.findViewById<EditText>(R.id.pw)
        val pwrep = view.findViewById<EditText>(R.id.pwrep)
        val register = view.findViewById<LinearLayout>(R.id.contbtn)

        register.setOnClickListener {
            register.isEnabled=false
            var valid = true
            if (mail.text.isEmpty()){
                valid=false
                mail.error =getString( R.string.errMissingMail)
            }else if (!mail.text.contains("@")){
                valid=false
                mail.error = getString(R.string.errMailNV)
            }


            if (pw.text.isEmpty()){
                valid=false
                pw.error = getString(R.string.errMissingPw)
            }else if(pw.text.length<8){
                valid = false
                pw.error = getString(R.string.errPWLEN)
            }else if (!pw.text.contains(Regex("[A-Z]"))){
                valid = false
                pw.error = getString(R.string.errPWUC)
            }else if(!pw.text.contains(Regex("[a-z]"))){
                valid = false
                pw.error = getString(R.string.errPWLC)
            }else if(!pw.text.contains(Regex("[0-9]"))){
                valid = false
                pw.error = getString(R.string.errPWNUM)
            }else if (!pw.text.contains(Regex("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]"))){
                valid = false
                pw.error = getString(R.string.errPWSYM)
            }


            if (pwrep.text.isEmpty()){
                valid=false
                pwrep.error = getString(R.string.errMissingPw)
            }else if(pw.text.toString() != pwrep.text.toString()){
                valid = false
                pwrep.error = getString(R.string.errPWREP)
            }

            if (valid){
                lifecycleScope.launch {
                    val res = AccountDatabase.register(mail.text.toString(), pw.text.toString())

                    if (res==null){
                        Toast.makeText(requireContext(), getString(R.string.errReg), Toast.LENGTH_SHORT).show()
                        requireActivity().finish()
                    }else if (res==""){
                        mail.error = getString(R.string.error)
                        register.isEnabled=true
                    }else{
                        (activity as EmptyActivity).openScreen("acccreation", false, res)
                    }
                }

            }else{
                register.isEnabled=true
            }

        }

        back.setOnClickListener {
            requireActivity().finish()
        }
    }
}