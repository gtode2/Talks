package com.example.talks

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.findViewTreeFullyDrawnReporterOwner
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.firestore

class RegisterActivity: AppCompatActivity() {
    lateinit var name:TextView
    lateinit var surname:TextView
    lateinit var username:TextView
    lateinit var dob:TextView
    lateinit var mail:TextView
    lateinit var password:TextView
    lateinit var back:Button
    lateinit var register:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)
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
                name.error="nome mancante"
                valid=false
            }
            if (surname.text.isEmpty()){
                surname.error="cognome mancante"
                valid=false
            }
            if (username.text.isEmpty()){
                username.error="username mancante"
                valid=false
            }
            if (dob.text.isEmpty()){
                dob.error="data di nascita mancante"
                valid=false
            }
            if (mail.text.isEmpty()){
                mail.error="indirizzo email mancante"
                valid=false
            }
            if (password.text.isEmpty()){
                password.error="nome mancante"
                valid=false
            }

            //controlli password
            if (!password.text.toString().contains(Regex("[A-Z]"))){
                password.error="password deve contenere almeno una lettera maiuscola"
                valid=false
            }
            if (!password.text.toString().contains(Regex("[a-z]"))){
                password.error="password deve contenere almeno una lettera minuscola"
                valid=false
            }
            if (!password.text.toString().contains(Regex("[^A-Za-z0-9]"))){
                password.error="password deve contenere almeno un simbolo speciale"
                valid=false
            }
            if (!password.text.toString().contains(Regex("[0-9]"))){
                password.error="password deve contenere almeno un carattere numerico"
                valid=false
            }

            //controllo email
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail.text.toString()).matches()){
                mail.error="indirizzo email non valido: ${mail.text}"
                valid=false
            }

            //controllo nome e cognome
            if (!name.text.toString().contains(Regex("^[a-zA-Z]+$"))){
                name.error="nome può contenere solo lettere"
                valid=false
            }
            if (!surname.text.toString().contains(Regex("^[a-zA-Z]+$"))){
                surname.error="cognome può contenere solo lettere"
                valid=false
            }


            if (valid){
                val db = Firebase.firestore
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail.text.toString(), password.text.toString())
                    .addOnCompleteListener{task->
                        if (task.isSuccessful){
                            val UID = FirebaseAuth.getInstance().currentUser!!.uid
                            Toast.makeText(this, "Registrazione completata",Toast.LENGTH_SHORT).show()

                            val user = hashMapOf(
                                "authid" to UID,
                                "name" to name.text.toString(),
                                "surname" to surname.text.toString(),
                                "username" to username.text.toString(),
                                "bday" to dob.text.toString()
                            )
                            db.collection("Utenti")
                                .document(UID)
                                .set(user)
                                .addOnFailureListener{documentReference->
                                    Toast.makeText(this, "errore inserimento in db",Toast.LENGTH_SHORT).show()//DA RIMUOVERE
                                    Log.d("aaa", documentReference.toString())
                                }

                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("From","Login")
                            intent.putExtra("uid", UID)
                            startActivity(intent)
                            finish()
                        }else if (task.exception is FirebaseAuthUserCollisionException){
                            Toast.makeText(this, "Indirizzo Email già in uso",Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this, "Registrazione fallita",Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
        back.setOnClickListener{
            finish()
        }
    }
}