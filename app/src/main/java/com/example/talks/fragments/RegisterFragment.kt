package com.example.talks.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.talks.R
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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
            var valid = true
            if (mail.text.isEmpty()){
                valid=false
                mail.error = R.string.errMissingMail.toString()
            }
            if (pw.text.isEmpty()){
                valid=false
                pw.error = R.string.errMissingPw.toString()
            }
            if (pwrep.text.isEmpty()){
                valid=false
                //mail.error = R.string.errMissingMail.toString()
            }

            //aggiungere verifica password e mail

            if (pw.text.toString() != pwrep.text.toString()) {
                Log.e("AAA", "pw diverse" )
                valid = false
                //errore password
            }

            Log.e("AAA", valid.toString() )


            if (valid){
                //registro utente
                lifecycleScope.launch {
                    val res = AccountDatabase.register(mail.text.toString(), pw.text.toString())
                    if (res!=""){
                        //procedo a creazione account
                        val fragment = AccountCreationFragment().apply{
                            arguments = Bundle().apply {
                                putString("uid", res)
                            }
                        }
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.emptyframe, fragment)
                            .commit()
                    }else{
                        //errore
                    }
                }

            }

        }

        back.setOnClickListener {
            //requireactivity homepage
            //tornare a userunlogged
            requireActivity().finish()
        }
    }
}