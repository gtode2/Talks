package com.example.talks

import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.talks.database.ImageDatabase
import com.example.talks.database.PostDatabase
import com.example.talks.database.TagDatabase
import com.example.talks.managers.ImageManager
import com.example.talks.managers.TagManager
import com.example.talks.singleton.ImageCache
import com.example.talks.singleton.UserID
import kotlinx.coroutines.launch


class PostCreationActivity: AppCompatActivity() {
    var nchar:Int?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.postcreation)

        val title = findViewById<EditText>(R.id.postTitle)
        val post = findViewById<EditText>(R.id.postText)
        val source = findViewById<EditText>(R.id.srcPC)
        val backBtn = findViewById<ImageView>(R.id.close)
        val rem = findViewById<TextView>(R.id.remchcount)
        val imgbtn = findViewById<LinearLayout>(R.id.selectImage)
        val imgblock = findViewById<ConstraintLayout>(R.id.imgprevblock)
        val imgrembtn = findViewById<ImageView>(R.id.imgrembtn)
        val imgtxt = findViewById<TextView>(R.id.imgtxt)
        val prev = findViewById<ImageView>(R.id.imgprev)
        val createPost = findViewById<Button>(R.id.postBtn)
        var Imguri:Uri?=null
        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            //se seleziono immagine -> rendo visible
            uri?.let {
                prev.setImageURI(it)
                imgblock.visibility= View.VISIBLE
                imgtxt.text = ContextCompat.getString(this, R.string.changeImg)
                Imguri=it
            }
        }
        val errCol = ContextCompat.getColor(this, R.color.error)
        val origCol = rem.textColors




        imgbtn.setOnClickListener{
            pickImageLauncher.launch("image/*")
        }

        imgrembtn.setOnClickListener {
            Imguri=null
            imgblock.visibility= View.GONE
            imgtxt.text = ContextCompat.getString(this, R.string.addImg)
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
                val uri = Imguri
                var img = ""
                val imgBool = if (uri!=null) true else false

                val PCres = PostDatabase.createPost(UID, post.text.toString(), source.text.toString(), title.text.toString(), imgBool)

                if (uri!=null && PCres!="-1"){
                    val ICres = ImageCache.add(this@PostCreationActivity, PCres, uri, false)
                    //gestione risposta ICres -> se false -> modifico post -> rimuovo img
                }

                //verifica tag
                val taglist = TagManager().validate(post.text.toString(), this@PostCreationActivity)


                //gestione post e errori upload
                if (PCres!="-1"){
                    //se creazione post eseguito correttamente -> carico tag
                    if (taglist.isNotEmpty()){
                        TagDatabase.addTag(taglist,PCres)
                    }

                }else{
                    Toast.makeText(this@PostCreationActivity, "si è verificato un errore", Toast.LENGTH_SHORT).show()
                    //gestire errore
                }

                finish()
            }


        }
    }
}