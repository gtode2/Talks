package com.example.talks.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.talks.MainActivity
import com.example.talks.R
import com.example.talks.database.ImageDatabase
import com.example.talks.singleton.ImageCache
import com.example.talks.singleton.UserID
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.launch

//creare viewmodel
class PSViewModel: ViewModel(){
    var imgUri:Uri?=null
    var wasEmpty:Boolean=false
}
class ProfilePictureSelectFragment: Fragment(R.layout.profilepictureselect) {
    private lateinit var viewModel: PSViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PSViewModel::class.java)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val change = arguments?.getBoolean("change")?:false
        val selectImage = view.findViewById<LinearLayout>(R.id.select)
        val continueBtn = view.findViewById<LinearLayout>(R.id.contbtn)
        val image = view.findViewById<ShapeableImageView>(R.id.image)
        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                image.setImageURI(it)
                viewModel.imgUri=it
            }
        }



        if (UserID.getUID()==null){
            Toast.makeText(requireContext(), getString(R.string.errReLog), Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }else{
            lifecycleScope.launch {
                val oldImg = ImageCache.get(UserID.getUID()!!, true)
                if (oldImg==null){
                    viewModel.wasEmpty=true
                }

                if (viewModel.imgUri!=null){
                    image.setImageURI(viewModel.imgUri)
                }else if (change){
                        image.setImageBitmap(oldImg)
                }
            }
        }


        continueBtn.setOnClickListener {
            continueBtn.isEnabled=false
            val uri = viewModel.imgUri
            if (uri!=null){
                lifecycleScope.launch {
                    val res = ImageCache.add(requireContext(), UserID.getUID()!!, uri, true)

                    if (res){
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }else{
                        Toast.makeText(requireContext(), getString(R.string.errImg), Toast.LENGTH_SHORT).show()
                        continueBtn.isEnabled=true
                    }
                }
            }else{
                //immagine non selezionata -> blocco e mostro errore
                if (!viewModel.wasEmpty){
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                    return@setOnClickListener
                }else{
                    Toast.makeText(requireContext(), getString(R.string.errEmptyPrPic), Toast.LENGTH_SHORT).show()
                    continueBtn.isEnabled=true
                }

            }
        }

        selectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

    }
}