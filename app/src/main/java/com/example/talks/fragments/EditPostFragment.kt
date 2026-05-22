package com.example.talks.fragments

import android.content.Intent
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
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.talks.EmptyActivity
import com.example.talks.R
import com.example.talks.data.PostData
import com.example.talks.database.PostDatabase
import com.example.talks.singleton.ImageCache
import com.example.talks.singleton.UserID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        val pagetitle = view.findViewById<TextView>(R.id.title)
        val title = view.findViewById<EditText>(R.id.postTitle)
        val text = view.findViewById<EditText>(R.id.postText)
        val remch = view.findViewById<TextView>(R.id.remchcount) //remaining characters count
        val srctext = view.findViewById<EditText>(R.id.srcPC)
        val imgbtn = view.findViewById<LinearLayout>(R.id.selectImage)
        val imgblock = view.findViewById<ConstraintLayout>(R.id.imgprevblock)
        val imgrembtn = view.findViewById<ImageView>(R.id.imgrembtn)
        val imgtxt = view.findViewById<TextView>(R.id.imgtxt)
        val imgprev = view.findViewById<ImageView>(R.id.imgprev)
        val backbtn = view.findViewById<ImageView>(R.id.close)
        val contbtn = view.findViewById<Button>(R.id.postBtn)

        var imgChanged:Boolean=false

        var Imguri:Uri?=null
        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            //se seleziono immagine -> rendo visible
            uri?.let {
                imgprev.setImageURI(it)
                imgblock.visibility= View.VISIBLE
                imgtxt.text = ContextCompat.getString(requireContext(), R.string.changeImg)
                imgChanged=true
                Imguri=it
            }
        }


        pagetitle.text= ContextCompat.getString(requireContext(), R.string.editpost)
        //estraggo uid
        uid = UserID.getUID()

        if (postId.isNullOrBlank()){
            //gestisci errore
        }


        // popolare pagina
        lifecycleScope.launch{
            val p = withContext(Dispatchers.IO){ PostDatabase.getPost(postId!!)}
            if (p.isEmpty()){
                //gestione errore
            }
            post = p[0]
            title.setText(post!!.title)
            text.setText(post!!.post)
            remch.setText("${500-post!!.post.length}/500")
            srctext.setText(post!!.source)

            val img = withContext(Dispatchers.IO){ImageCache.get("image${postId}")}
            if (img==null){
                imgprev.visibility=View.GONE
            }else{
                imgprev.setImageBitmap(img)
                imgblock.visibility= View.VISIBLE
                imgtxt.text = ContextCompat.getString(requireContext(), R.string.changeImg)
            }
        }


        text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = text.text.toString().length
                remch.text= length.toString()+"/500"
                if (length==500){
                    //remch.setTextColor(errCol)
                    remch.setTypeface(null, Typeface.BOLD)
                }else{
                    //remch.setTextColor(origCol)
                    remch.setTypeface(null, Typeface.NORMAL)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // binding edit
        contbtn.setOnClickListener{
            var edit=PostData()
            if (title.text.toString()!=post!!.title){
                edit.title=title.text.toString()
            }
            if (text.text.toString()!=post!!.post){
                edit.post=text.text.toString()
            }
            if (srctext.text.toString()!=post!!.source){
                edit.source=srctext.text.toString()
            }
            if(imgChanged){
                //se aggiungo/rimuovo img -> modifica flag in post
                edit.image=if(Imguri==null) false else true
            }

            //gestire img

            val tmp=PostData()
            if(edit!=tmp || imgChanged){
                //verifica imgchanged -> cambio foto -> non modifico flag in post
                if (edit!=tmp){
                    //non modifico se cambio solo immagine -> boolean in post resta uguale
                    lifecycleScope.launch {
                        val res = withContext(Dispatchers.IO){PostDatabase.editPost(uid!!, postId!!, edit)}
                        when(res){
                            0 -> {
                                Toast.makeText(context, "Post modificato correttamente", Toast.LENGTH_SHORT).show()
                                //chiudi schermata
                            }
                            1-> {Toast.makeText(context, "Impossibile trovare il post. potrebbe esser stato eliminato", Toast.LENGTH_SHORT).show()}
                            else-> {Toast.makeText(context, "Si è verificato un errore, riprovare", Toast.LENGTH_SHORT).show()}
                        }
                    }
                }
                if (imgChanged){
                    lifecycleScope.launch(Dispatchers.IO) {
                        val res = ImageCache.add(requireContext(), postId!!, Imguri!!, false)
                        //gestione res
                    }
                }

            }else{
                //errore
            }
        }
        backbtn.setOnClickListener {
            val intent = Intent(requireContext(),EmptyActivity::class.java)
                .putExtra("screen","your")
            startActivity(intent)
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            val intent = Intent(requireContext(),EmptyActivity::class.java)
                .putExtra("screen","your")
            startActivity(intent)
        }
        imgbtn.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        imgrembtn.setOnClickListener {
            Imguri=null
            imgChanged=true
            imgblock.visibility= View.GONE
            imgtxt.text = ContextCompat.getString(requireContext(), R.string.addImg)
        }

    }
}