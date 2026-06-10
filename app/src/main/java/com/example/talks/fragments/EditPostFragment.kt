package com.example.talks.fragments

import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.talks.R
import com.example.talks.data.PostData
import com.example.talks.database.PostDatabase
import com.example.talks.singleton.ImageCache
import com.example.talks.singleton.UserID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditPostFragment:Fragment(R.layout.postcreation) {
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
        val contbtn = view.findViewById<LinearLayout>(R.id.postBtn)
        val buttontxt = view.findViewById<TextView>(R.id.postbtntxt)
        val frame = view.findViewById<FrameLayout>(R.id.frame)
        var imgChanged=false

        var Imguri:Uri?=null
        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imgprev.setImageURI(it)
                imgblock.visibility= View.VISIBLE
                imgtxt.text = getString(R.string.changeImg)
                imgChanged=true
                Imguri=it
            }
        }


        pagetitle.text= getString( R.string.editpost)
        buttontxt.text=getString(R.string.save)

        uid = UserID.getUID()

        if (postId.isNullOrBlank()){
            Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }


        lifecycleScope.launch{
            val p = withContext(Dispatchers.IO){ PostDatabase.getPost(postId!!)}
            if (p.isEmpty()){
                val view = layoutInflater.inflate(R.layout.errorpage, frame, true)
                view.findViewById<TextView>(R.id.text).text=getString(R.string.notifnotlogged)
            }
            post = p[0]
            title.setText(post!!.title)
            text.setText(post!!.post)
            remch.setText("${post!!.post.length}/500")
            srctext.setText(post!!.source)

            val img = withContext(Dispatchers.IO){ImageCache.get("image${postId}")}
            if (img==null){
                imgblock.visibility=View.GONE
            }else{
                imgprev.setImageBitmap(img)
                imgblock.visibility= View.VISIBLE
                imgtxt.text =getString(R.string.changeImg)
            }
        }


        text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = text.text.toString().length
                remch.text= length.toString()+"/500"
                if (length==500){
                    remch.setTypeface(null, Typeface.BOLD)
                }else{
                    remch.setTypeface(null, Typeface.NORMAL)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

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


            val tmp = PostData()
            var cont=false

            lifecycleScope.launch {
                if (tmp!=edit){
                    val res = withContext(Dispatchers.IO){PostDatabase.editPost(uid!!, postId!!, edit)}
                    when(res){
                        0 -> {
                            Toast.makeText(context, getString(R.string.posteditcomp), Toast.LENGTH_SHORT).show()
                            cont=true
                        }
                        1-> {
                            Toast.makeText(context, getString(R.string.errpostnf), Toast.LENGTH_SHORT).show()
                        }
                        else-> {
                            Toast.makeText(context, getString(R.string.error), Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    cont = true
                }


                if (cont){
                    if(post!!.image){
                        if(imgChanged) {
                            if (Imguri == null) {
                                val res = withContext(Dispatchers.IO) {
                                    ImageCache.remove(false,postId!!)
                                }
                                if (!res) {
                                    Toast.makeText(requireContext(),getString(R.string.errImgRem),Toast.LENGTH_SHORT).show()
                                    cont = false
                                } else {
                                    cont = true
                                }
                            } else {
                                val res = withContext(Dispatchers.IO){ImageCache.add(requireContext(), postId!!, Imguri!!, false)}
                                if(!res){
                                    Toast.makeText(requireContext(), getString(R.string.errImgEdit), Toast.LENGTH_SHORT).show()
                                    cont = false
                                }else{
                                    cont=true
                                }
                            }
                        }
                    }else{
                        var res = withContext(Dispatchers.IO){ImageCache.add(requireContext(), postId!!, Imguri!!, false)}
                        if (!res){
                            Toast.makeText(requireContext(), getString(R.string.errImgAdd), Toast.LENGTH_SHORT).show()
                            cont = false
                        }else{
                            res = withContext(Dispatchers.IO) { PostDatabase.editImgPost(postId!!, true) }
                            if (!res){
                                //errore aggiunta a db
                                //rimuovo da cache
                                withContext(Dispatchers.IO) { ImageCache.remove(false, postId!!, true) }
                                Toast.makeText(requireContext(), getString(R.string.errImgAdd),Toast.LENGTH_SHORT).show()
                                cont = false

                            }else{
                                cont=true
                            }
                        }
                    }

                }
                if (cont){
                    parentFragmentManager.popBackStack()
                }else{
                    contbtn.isEnabled=true
                }
            }
        }



        backbtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        imgbtn.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        imgrembtn.setOnClickListener {
            Imguri=null
            imgChanged=true
            imgblock.visibility= View.GONE
            imgtxt.text = getString(R.string.addImg)
        }

    }
}