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
        val register = view.findViewById<Button>(R.id.contbtn)

        register.setOnClickListener {
            register.isEnabled=false
            var valid = true
            if (mail.text.isEmpty()){
                valid=false
                mail.error = R.string.errMissingMail.toString()
            }else if (!mail.text.contains("@")){
                valid=false
                mail.error = R.string.errInvalidMail.toString() //
            }


            if (pw.text.isEmpty()){
                valid=false
                pw.error = R.string.errMissingPw.toString()
            }else if(pw.text.length<8){
                valid = false
                pw.error = R.string.errPWLEN.toString()
            }else if (!pw.text.contains(Regex("[A-Z]"))){
                valid = false
                pw.error = R.string.errPWUC.toString()
            }else if(!pw.text.contains(Regex("[a-z]"))){
                valid = false
                pw.error = R.string.errPWLC.toString()
            }else if(!pw.text.contains(Regex("[0-9]"))){
                valid = false
                pw.error = R.string.errPWNUM.toString()
            }else if (!pw.text.contains(Regex("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]"))){
                valid = false
                pw.error = R.string.errPWSYM.toString()
            }


            if (pwrep.text.isEmpty()){
                valid=false
                pwrep.error = R.string.errMissingPw.toString()
            }else if(pw.text.toString() != pwrep.text.toString()){
                valid = false
                pwrep.error = R.string.errPWREP.toString()
            }

            if (valid){
                lifecycleScope.launch {
                    val res = AccountDatabase.register(mail.text.toString(), pw.text.toString())

                    if (res==null){
                        Toast.makeText(requireContext(), R.string.errReg.toString(), Toast.LENGTH_SHORT).show()
                        requireActivity().finish()
                    }else if (res==""){
                        mail.error = R.string.error.toString()
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