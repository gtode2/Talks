package com.example.talks

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.talks.database.AccountDatabase
import com.example.talks.singleton.UserID
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

class RegisterActivity: AppCompatActivity() {
    /*
    lateinit var name:TextView
    lateinit var surname:TextView
    lateinit var username:TextView
    lateinit var dob:TextView
    lateinit var mail:TextView
    lateinit var password:TextView
    lateinit var back:Button
    lateinit var register:Button
    */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        val back = findViewById<ImageView>(R.id.backbtn)
        val googleBtn = findViewById<LinearLayout>(R.id.google)
        val mail = findViewById<EditText>(R.id.email)
        val pw = findViewById<EditText>(R.id.pw)
        val pwrep = findViewById<EditText>(R.id.pwrep)
        val register = findViewById<LinearLayout>(R.id.contbtn)


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
            if (pw.text != pwrep.text){
                valid=false
                //errore password
            }

            if (valid){
                //registro utente
                lifecycleScope.launch {
                    val res = AccountDatabase.register(mail.text.toString(), pw.text.toString())
                    if (res){
                        //procedo a creazione account
                    }else{
                        //errore
                    }
                }

            }

        }
        back.setOnClickListener{
            finish()
        }
        /*
        name = findViewById(R.id.nameET)
        surname = findViewById(R.id.surnameET)
        username = findViewById(R.id.usernameET)
        dob = findViewById(R.id.dateET)
        mail = findViewById(R.id.emailET)
        password = findViewById(R.id.pwET)
        back = findViewById(R.id.backbtn)
        register = findViewById(R.id.signupBtn)

        register.setOnClickListener{
            var valid=true
            if (name.text.isEmpty()){
                name.error= R.string.errMissingName.toString()
                valid=false
            }
            if (surname.text.isEmpty()){
                surname.error= R.string.errMissingSurname.toString()
                valid=false
            }
            if (username.text.isEmpty()){
                username.error=R.string.errMissingUsername.toString()
                valid=false
            }
            if (dob.text.isEmpty()){
                dob.error=R.string.errMissingDOB.toString()
                valid=false
            }
            if (mail.text.isEmpty()){
                mail.error=R.string.errMissingMail.toString()
                valid=false
            }
            if (password.text.isEmpty()){
                password.error=R.string.errMissingPw.toString()
                valid=false
            }

            //controlli password
            if (!password.text.toString().contains(Regex("[A-Z]"))){
                password.error=R.string.errPWLC.toString()
                valid=false
            }
            if (!password.text.toString().contains(Regex("[a-z]"))){
                password.error=R.string.errPWUC.toString()
                valid=false
            }
            if (!password.text.toString().contains(Regex("[^A-Za-z0-9]"))){
                password.error=R.string.errPWSYM.toString()
                valid=false
            }
            if (!password.text.toString().contains(Regex("[0-9]"))){
                password.error=R.string.errPWNUM.toString()
                valid=false
            }

            //controllo email
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail.text.toString()).matches()){
                mail.error=R.string.errMailNV.toString()
                valid=false
            }

            //controllo nome e cognome
            if (!name.text.toString().contains(Regex("^[a-zA-Z]+$"))){
                name.error=R.string.errNameNL.toString()
                valid=false
            }
            if (!surname.text.toString().contains(Regex("^[a-zA-Z]+$"))){
                surname.error=R.string.errSurnameNL.toString()
                valid=false
            }


            if (valid){
                val db = Firebase.firestore
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail.text.toString(), password.text.toString())
                    .addOnCompleteListener{task->
                        if (task.isSuccessful){
                            val UID = FirebaseAuth.getInstance().currentUser!!.uid
                            Toast.makeText(this, R.string.succReg.toString(),Toast.LENGTH_SHORT).show()

                            val user = hashMapOf(
                                "authid" to UID,
                                "name" to name.text.toString(),
                                "surname" to surname.text.toString(),
                                "bday" to dob.text.toString(),
                                "followers" to 0,
                                "likes" to mutableMapOf<String,Boolean>(),
                                "saved" to mutableMapOf<String, Boolean>(),
                                "followed" to mutableMapOf<String, Boolean>()
                            )

                            //verificare username duplicati

                            db.collection("Users")
                                .document(username.text.toString())
                                .set(user)
                                .addOnFailureListener{documentReference->
                                    Toast.makeText(this, "errore inserimento in db",Toast.LENGTH_SHORT).show()//DA RIMUOVERE
                                    //in questo caso eliminare utente
                                    //print messaggio errore
                                }

                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("From","Login")
                            UserID.setUID(username.text.toString())
                            startActivity(intent)
                            finish()
                        }else if (task.exception is FirebaseAuthUserCollisionException){
                            Toast.makeText(this, R.string.errInvalidMail.toString(),Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this, R.string.errDBReg.toString(),Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
        */

    }
}