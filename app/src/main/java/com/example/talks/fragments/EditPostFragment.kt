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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.talks.R
import com.example.talks.data.PostData
import com.example.talks.database.PostDatabase
import com.example.talks.singleton.ImageCache
import com.example.talks.singleton.UserID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.ViewModelProvider
import com.example.talks.database.ImageDatabase


class EPViewModel: ViewModel() {
    var imguri:Uri? = null
    var imgChanged:Boolean = false
}
class EditPostFragment:Fragment(R.layout.postcreation) {
    private var postId:String?=null
    private var post:PostData?=null
    private var uid:String?=null
    private lateinit var viewModel: EPViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postId = arguments?.getString("id")
        viewModel = ViewModelProvider(this).get(EPViewModel::class.java)
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

        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imgprev.setImageURI(it)
                imgblock.visibility= View.VISIBLE
                imgtxt.text = getString(R.string.changeImg)
                viewModel.imgChanged=true
                viewModel.imguri=it
            }
        }


        pagetitle.text= getString( R.string.editpost)
        buttontxt.text=getString(R.string.save)

        uid = UserID.getUID()

        //sposto in oncreate?
        if (postId.isNullOrBlank()){
            Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }else{
            lifecycleScope.launch{
                val p = PostDatabase.getPost(postId!!)
                if (p==null){

                }else  if (p.isEmpty()){
                    val view = layoutInflater.inflate(R.layout.errorpage, frame, true)
                    view.findViewById<TextView>(R.id.text).text=getString(R.string.notifnotlogged)
                }else{
                    post = p[0]
                    title.setText(post!!.title)
                    text.setText(post!!.post)
                    remch.setText("${post!!.post.length}/500")
                    srctext.setText(post!!.source)

                    if (viewModel.imgChanged){
                        if (viewModel.imguri==null){
                            imgblock.visibility=View.GONE
                            imgtxt.text=getString(R.string.addImg)
                        }else{
                            imgprev.setImageURI(viewModel.imguri)
                            imgblock.visibility=View.VISIBLE
                            imgtxt.text=getString(R.string.changeImg)
                        }
                    }else{
                        val img = ImageCache.get(postId!!, false, post!!.imgTimestamp)
                        if (img==null){
                            imgblock.visibility=View.GONE
                            imgtxt.text=getString(R.string.addImg)
                        }else{
                            imgprev.setImageBitmap(img)
                            imgblock.visibility=View.VISIBLE
                            imgtxt.text=getString(R.string.changeImg)
                        }
                    }
                }
            }


            text.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val length = text.text.toString().length
                    remch.text= "$length/500"
                    if (length==500){
                        remch.setTypeface(null, Typeface.BOLD)
                    }else{
                        remch.setTypeface(null, Typeface.NORMAL)
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })

            contbtn.setOnClickListener{
                val edit=PostData()
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
                            //post contiene immagine
                            if(viewModel.imgChanged) {
                                //immagine modificata
                                if (viewModel.imguri == null) {
                                    //immagine vuota -> rimossa
                                    val res = ImageDatabase.remove(false, postId!!)
                                    if (!res) {
                                        //errore in rimozione
                                        Toast.makeText(requireContext(),getString(R.string.errImgRem),Toast.LENGTH_SHORT).show()
                                        cont = false
                                    } else {
                                        //rimossa correttamente -> rimuovo anche da cache
                                        ImageCache.remove(false,postId!!)
                                        cont = true
                                    }
                                } else {
                                    //immagine non vuota -> aggiungo
                                    val res = ImageCache.add(requireContext(), postId!!, viewModel.imguri!!, false)
                                    if(!res){
                                        //errore in aggiunta
                                        Toast.makeText(requireContext(), getString(R.string.errImgEdit), Toast.LENGTH_SHORT).show()
                                        cont = false
                                    }else{
                                        //aggiunta correttamente
                                        cont=true
                                    }
                                }
                            }
                        }else{
                            //post non ha immagine
                            if (viewModel.imguri!=null){
                                var res = ImageCache.add(requireContext(), postId!!, viewModel.imguri!!, false)
                                if (!res){
                                    Toast.makeText(requireContext(), getString(R.string.errImgAdd), Toast.LENGTH_SHORT).show()
                                    cont = false
                                }else{
                                    res = PostDatabase.editImgPost(postId!!, true)
                                    if (!res){
                                        //errore aggiunta a db
                                        //rimuovo da cache

                                        ImageCache.remove(false, postId!!)
                                        val imgres = ImageDatabase.remove(false, postId!!)
                                        if (!imgres){
                                            Toast.makeText(requireContext(), getString(R.string.errImgRem), Toast.LENGTH_SHORT).show()
                                        }
                                        Toast.makeText(requireContext(), getString(R.string.errImgAdd),Toast.LENGTH_SHORT).show()
                                        cont = false

                                    }else{
                                        cont=true
                                    }
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
                viewModel.imguri=null
                viewModel.imgChanged=true
                imgblock.visibility= View.GONE
                imgtxt.text = getString(R.string.addImg)
            }
        }
    }
}