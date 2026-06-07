package com.example.talks.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.talks.EmptyActivity
import com.example.talks.R
import com.example.talks.database.AccountDatabase
import com.example.talks.singleton.UserID
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AccountCreationFragment: Fragment(R.layout.accountcreation) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cont = view.findViewById<Button>(R.id.cont)
        val nameET = view.findViewById<EditText>(R.id.name)
        val surnameET = view.findViewById<EditText>(R.id.surname)
        val usernameET = view.findViewById<EditText>(R.id.username)
        val dobET = view.findViewById<EditText>(R.id.dob)
        var uid = arguments?.getString("uid")?:""

        if (uid==""){
            if (UserID.getTemp()==null){
                parentFragmentManager.beginTransaction()
                    .replace(R.id.emptyframe, LoginFragment())
                    .commit()
            }else{
                //utente ha interrotto registrazione -> login salva uid firebase
                uid = UserID.getTemp()!!
            }
        }

        dobET.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getText(R.string.datesel))
                .build()
            picker.show(parentFragmentManager, "DATE_PICKER")
            picker.addOnPositiveButtonClickListener {sel ->
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                dobET.setText(formatter.format(Date(sel)))
            }
        }

        cont.setOnClickListener {

            val name = nameET.text.toString()
            val surname = surnameET.text.toString()
            val username = usernameET.text.toString()
            val dob = dobET.text.toString()

            var valid=true
            if (name.isEmpty()){
                nameET.error= getString(R.string.errMissingName)
                valid=false
            }else{
                val lett = name.matches(Regex("^[\\p{L} ]+$"))
                if (!lett){
                    nameET.error=getString(R.string.errNameNL)
                    valid=false
                }
            }


            if (surname.isEmpty()){
                surnameET.error= getString(R.string.errMissingSurname)
                valid=false
            }else{
                val lett = surname.matches(Regex("^[\\p{L} ]+$"))
                if (!lett){
                    surnameET.error=getString(R.string.errSurnameNL)
                    valid=false
                }
            }

            if (username.isEmpty()){
                usernameET.error=getString(R.string.errMissingUsername)
                valid=false
            }else{
                if (username.contains("@") || username.contains(" ")){
                    usernameET.error=getString(R.string.errInvalidUsername)
                    valid=false
                }
            }


            if (dob.isEmpty()){
                dobET.error=getString(R.string.errMissingDOB)
                valid=false
            }else{
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = formatter.parse(dobET.text.toString())
                val today = Date()
                if (date.after(today)){
                    dobET.error=getString(R.string.errinvalidDate)
                    valid=false
                }
            }


            if (valid){
                lifecycleScope.launch {
                    val res = withContext(Dispatchers.IO) {AccountDatabase.createAccount(name, surname, username, dob, uid)}
                    if (res==0){
                        UserID.setUID(username)
                        (activity as EmptyActivity).openScreen("pps", false)
                    }else{
                        if (res==-1){
                            Toast.makeText(context, getString(R.string.error), Toast.LENGTH_SHORT).show()
                        }else if (res==-2){
                            usernameET.error=getString(R.string.errExUsername)
                        }
                    }
                }

            }
        }

    }
}