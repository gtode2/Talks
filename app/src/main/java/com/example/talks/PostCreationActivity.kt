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
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.talks.database.ImageDatabase
import com.example.talks.database.PostDatabase
import com.example.talks.database.TagDatabase
import com.example.talks.managers.ImageManager
import com.example.talks.managers.TagManager
import com.example.talks.singleton.UserID
import kotlinx.coroutines.launch


class PostCreationActivity: AppCompatActivity() {
    var nchar:Int?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.postcreation)

        val title = findViewById<EditText>(R.id.titleet)
        val post = findViewById<EditText>(R.id.postet)
        val source = findViewById<EditText>(R.id.srcPC)
        val backBtn = findViewById<Button>(R.id.pcBackbtn)
        val rem = findViewById<TextView>(R.id.remchcount)
        val imgbtn = findViewById<Button>(R.id.imgbtn)
        val prev = findViewById<ImageView>(R.id.imgprev)
        val createPost = findViewById<Button>(R.id.pcContinue)
        var Imguri:Uri?=null
        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                prev.setImageURI(it)
                Imguri=it
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
            val UID = UserID.getUID()
            if (UID.isNullOrBlank()){
                //gestione errore
                return@setOnClickListener
            }

            //verifica elementi
            if (title.text.isBlank() || post.text.isBlank()){
                Toast.makeText(this, R.string.errMissingTitleOrText, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                //verifica immagine
                Log.e("AAA", Imguri.toString() )
                var img = ""
                if (Imguri!=null){
                     img = ImageManager.compressor(this@PostCreationActivity, Imguri)
                    if (img==""){
                        //gestione errore
                        Toast.makeText(this@PostCreationActivity, "si è verificato un errore", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                }
                var imgbool = if (img!="") true else false

                //verifica tag
                val taglist = TagManager().validate(post.text.toString(), this@PostCreationActivity)

                //crea post
                //res = post id | -1

                var res = PostDatabase.createPost(UID, post.text.toString(), source.text.toString(), title.text.toString(), imgbool)

                //gestione post e errori upload
                if (res!="-1"){
                    //se upload post eseguito correttamente -> upload img e tag
                    if (taglist.isNotEmpty()){
                        TagDatabase.addTag(taglist,res)
                    }
                    if (img!=""){
                        ImageDatabase.add(img, res)
                    }


                }else{
                    Toast.makeText(this@PostCreationActivity, "si è verificato un errore", Toast.LENGTH_SHORT).show()
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