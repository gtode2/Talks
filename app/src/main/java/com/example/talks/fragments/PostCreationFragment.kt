package com.example.talks.fragments

import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.talks.R
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.talks.database.PostDatabase
import com.example.talks.singleton.ImageCache
import com.example.talks.singleton.UserID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.ViewModelProvider



class PCViewModel: ViewModel() {
    var imgUri:Uri? = null
}
class PostCreationFragment: Fragment(R.layout.postcreation) {
    var nchar:Int?=null
    private lateinit var viewModel: PCViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PCViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = view.findViewById<EditText>(R.id.postTitle)
        val post = view.findViewById<EditText>(R.id.postText)
        val source = view.findViewById<EditText>(R.id.srcPC)
        val backBtn = view.findViewById<ImageView>(R.id.close)
        val rem = view.findViewById<TextView>(R.id.remchcount)
        val imgbtn = view.findViewById<LinearLayout>(R.id.selectImage)
        val imgblock = view.findViewById<ConstraintLayout>(R.id.imgprevblock)
        val imgrembtn = view.findViewById<ImageView>(R.id.imgrembtn)
        val imgtxt = view.findViewById<TextView>(R.id.imgtxt)
        val prev = view.findViewById<ImageView>(R.id.imgprev)
        val createPost = view.findViewById<LinearLayout>(R.id.postBtn)
        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                prev.setImageURI(it)
                imgblock.visibility= View.VISIBLE
                imgtxt.text = ContextCompat.getString(requireContext(), R.string.changeImg)
                viewModel.imgUri=it
            }
        }
        val errCol = ContextCompat.getColor(requireContext(), R.color.error)
        val origCol = rem.textColors

        viewModel.imgUri?.let {
            prev.setImageURI(it)
            imgblock.visibility= View.VISIBLE
            imgtxt.text = ContextCompat.getString(requireContext(), R.string.changeImg)
        }


        imgbtn.setOnClickListener{
            pickImageLauncher.launch("image/*")
        }

        imgrembtn.setOnClickListener {
            viewModel.imgUri=null
            imgblock.visibility= View.GONE
            imgtxt.text = ContextCompat.getString(requireContext(), R.string.addImg)
        }

        nchar = 0

        post.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = post.text.toString().length
                rem.text= "$length/500"
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
            requireActivity().finish()
        }

        createPost.setOnClickListener{
            createPost.isEnabled=false
            val uid = UserID.getUID()
            if (uid.isNullOrBlank()){
                Toast.makeText(requireContext(), getString(R.string.errAccount), Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }

            if (title.text.isBlank() || post.text.isBlank()){
                Toast.makeText(requireContext(), R.string.errMissingTitleOrText, Toast.LENGTH_SHORT).show()
                createPost.isEnabled=true
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val uri = viewModel.imgUri
                val imgBool = uri != null

                val pcres = withContext(Dispatchers.IO){PostDatabase.createPost(
                    uid!!,
                    post.text.toString(),
                    source.text.toString(),
                    title.text.toString(),
                    imgBool
                )}

                if (uri != null && pcres != "-1") {
                    val icres = ImageCache.add(requireContext(), pcres, uri, false)
                    if (!icres) {
                        Toast.makeText(requireContext(),getString(R.string.errImgAdd),Toast.LENGTH_SHORT).show()
                    }
                }


                if (pcres == "-1") {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                    createPost.isEnabled = true

                }else{
                    viewModel.imgUri = null
                    requireActivity().finish()
                }
            }


        }


    }
}