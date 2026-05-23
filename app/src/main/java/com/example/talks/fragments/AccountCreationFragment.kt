package com.example.talks.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.talks.R
import com.example.talks.database.AccountDatabase
import com.example.talks.database.UserDatabase
import com.example.talks.singleton.UserID
import kotlinx.coroutines.launch


class AccountCreationFragment: Fragment(R.layout.accountcreation) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cont = view.findViewById<Button>(R.id.cont)
        val nameET = view.findViewById<EditText>(R.id.name)
        val surnameET = view.findViewById<EditText>(R.id.surname)
        val usernameET = view.findViewById<EditText>(R.id.username)
        val dobET = view.findViewById<EditText>(R.id.dob)

        cont.setOnClickListener {

            val name = nameET.text.toString()
            val surname = surnameET.text.toString()
            val username = usernameET.text.toString()
            val dob = dobET.text.toString()

            val uid = arguments?.getString("uid")?:""

            //verifica dati
            var valid=true
            if (name.isEmpty()){
                nameET.error= R.string.errMissingName.toString()
                valid=false
            }
            if (surname.isEmpty()){
                surnameET.error= R.string.errMissingSurname.toString()
                valid=false
            }
            if (username.isEmpty()){
                usernameET.error=R.string.errMissingUsername.toString()
                valid=false
            }
            if (dob.isEmpty()){
                dobET.error=R.string.errMissingDOB.toString()
                valid=false
            }

            //aggiungere verifica dob e testi


            if (valid){
                lifecycleScope.launch {
                    val res = AccountDatabase.createAccount(name, surname, username, dob, uid)
                    if (res){
                        UserID.setUID(username)
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.emptyframe, ProfilePictureSelectFragment())
                            .addToBackStack(null)
                            .commit()
                    }else{
                        //errore
                    }
                }

            }
        }

    }
}