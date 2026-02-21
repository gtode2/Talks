package com.example.talks

import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.talks.data.PostData
import com.example.talks.database.PostDatabase
import com.example.talks.database.TagDatabase
import com.example.talks.managers.TagManager
import com.example.talks.singleton.AppSettings
import com.example.talks.singleton.UserID
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch


class PostCreationActivity: AppCompatActivity() {
    var nchar:Int?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.postcreation)
        val settings = this.applicationContext as AppSettings

        val title = findViewById<EditText>(R.id.titleet)
        val post = findViewById<EditText>(R.id.postet)
        val source = findViewById<EditText>(R.id.srcPC)
        val backBtn = findViewById<Button>(R.id.pcBackbtn)
        val rem = findViewById<TextView>(R.id.remchcount)
        val imgbtn = findViewById<Button>(R.id.imgbtn)
        val prev = findViewById<ImageView>(R.id.imgprev)
        val createPost = findViewById<Button>(R.id.pcContinue)
        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                prev.setImageURI(it)  // o qualsiasi altra cosa tu voglia fare con l'immagine
            }
        }
        val errCol = ContextCompat.getColor(this, R.color.error)
        val origCol = rem.textColors

        imgbtn.setOnClickListener{
            pickImageLauncher.launch("image/*")
        }
        nchar = 0
        post.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = post.text.toString().length
                rem.text= length.toString()+"/500"
                if (length==500){
                    rem.setTextColor(errCol)
                    rem.setTypeface(null, Typeface.BOLD)
                }else{
                    rem.setTextColor(origCol)
                    rem.setTypeface(null, Typeface.NORMAL)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        backBtn.setOnClickListener{
            finish()
        }
        createPost.setOnClickListener{
            //verifica elementi
            if (title.text.isBlank() || post.text.isBlank()){
                Toast.makeText(this, R.string.errMissingTitleOrText, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val taglist = TagManager().validate(post.text.toString(), this@PostCreationActivity)
                //pubblica post


                val UID = UserID.getUID()
                if (UID.isNullOrBlank()){
                    //gestione errore
                    return@launch
                }

                var res = PostDatabase.createPost(UID!!, post.text.toString(), source.text.toString(), title.text.toString())
                Log.e("AAA", "taglist"+taglist.toString())
                Log.e("AAA", "pca res"+res)
                if (res!="-1" && taglist.isNotEmpty()){
                    //aggiungi eventuali notifiche tag
                    TagDatabase.addTag(taglist,res)
                }else if (res=="-1"){
                    //gestire errore
                }

                finish()
            }






            //esegue verifica e compressione immagine
            //invia info a DB



            //gestione eventuali tag nel testo del post
            //riceve conferma e reindirizza a homepage

        }
    }
}