package com.example.talks.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.talks.singleton.AppSettings
import com.example.talks.R
import com.example.talks.data.PostData
import com.example.talks.database.PostDatabase
import com.example.talks.singleton.UserID

class EditPostFragment:Fragment(R.layout.postcreation) {
    //con postcreation definitivo decidere se modificare testo aggiunta o se duplicare e creare nuova pagina con label diverse
    var postId:String?=null
    var post:PostData?=null
    var uid:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postId = arguments?.getString("id")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = view.findViewById<EditText>(R.id.titleet)
        val text = view.findViewById<EditText>(R.id.postet)
        val remch = view.findViewById<TextView>(R.id.remchcount) //remaining characters count
        val srctext = view.findViewById<EditText>(R.id.srcPC)
        val imgbtn = view.findViewById<Button>(R.id.imgbtn)
        val imgprev = view.findViewById<ImageView>(R.id.imgprev)
        val backbtn = view.findViewById<Button>(R.id.pcBackbtn)
        val contbtn = view.findViewById<Button>(R.id.pcContinue)

        //estraggo uid
        val settings = requireActivity().applicationContext as AppSettings
        uid = UserID.getUID()

        if (postId.isNullOrBlank()){
            //gestisci errore
        }

        PostDatabase.getPost(postId!!){ res->
            if (res.isEmpty()){
                //gestione errore
            }
            post = res[0]
            title.setText(post!!.title)
            text.setText(post!!.post)
            remch.setText("${500-post!!.post.length}/500")
            srctext.setText(post!!.source)
        }
        // popolare pagina

        // binding edit
        contbtn.setOnClickListener{
            var edit:PostData=PostData()
            if (title.text.toString()!=post!!.title){
                edit.title=title.text.toString()
            }
            if (text.text.toString()!=post!!.post){
                edit.post=text.text.toString()
            }
            if (srctext.text.toString()!=post!!.source){
                edit.source=srctext.text.toString()
            }

            //gestire img

            val tmp=PostData()
            if(edit!=tmp){
                PostDatabase.editPost(uid!!, postId!!, edit){res->
                    if (res==0){
                        //chiudi schermata
                        Toast.makeText(context, "Post modificato correttamente", Toast.LENGTH_SHORT).show()
                    }else if (res==1){
                        Toast.makeText(context, "Impossibile trovare il post. potrebbe esser stato eliminato", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(context, "Si è verificato un errore, riprovare", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                //errore
            }
        }
        // back()
    }
}