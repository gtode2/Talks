package com.example.talks

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.talks.repository.LikeRepository
import com.example.talks.singleton.UserID
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.initialize

class LoginActivity: AppCompatActivity() {
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        var loginBtn = findViewById<Button>(R.id.loginBtn)
        //var backBtn = findViewById<Button>(R.id.backBtn)
        var mail = findViewById<EditText>(R.id.emailET)
        var password = findViewById<EditText>(R.id.pwET)
        Firebase.initialize(this)
        auth = FirebaseAuth.getInstance()
        loginBtn.setOnClickListener{
            if (mail.text.isEmpty()){
                mail.error=getString(R.string.missingmail)
            }
            else if (password.text.isEmpty()){
                password.error=getString(R.string.missingpassword)
            }else{
                auth.signInWithEmailAndPassword(mail.text.toString(), password.text.toString())
                    .addOnCompleteListener(this){
                        task->
                        if (task.isSuccessful){
                            val user=auth.currentUser
                            val uid = user!!.uid
                            var userTag = ""

                            //recuperare tag utente da Utenti
                            FirebaseFirestore.getInstance()
                                .collection("Users")
                                .whereEqualTo("authid", uid)
                                .get()
                                .addOnSuccessListener { res ->
                                    for (doc in res){
                                        userTag = doc.id
                                    }
                                    Toast.makeText(this,"Login eseguito", Toast.LENGTH_SHORT).show()
                                    //LikeRepository.loadLikes(userTag)
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.putExtra("From","Login")
                                    UserID.setUID(userTag)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener {
                                    Log.e("NVNC", "errore caricamento documento")
                                    return@addOnFailureListener
                                }
                        }else{
                            Toast.makeText(this,"Errore nel login", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

        }
        /*backBtn.setOnClickListener{
            finish()
        }*/
    }




}