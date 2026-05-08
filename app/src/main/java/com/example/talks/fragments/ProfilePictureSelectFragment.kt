package com.example.talks.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.talks.MainActivity
import com.example.talks.R
import com.example.talks.database.ImageDatabase
import com.example.talks.managers.ImageManager
import com.example.talks.singleton.ImageCache
import com.example.talks.singleton.UserID
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.launch

class ProfilePictureSelectFragment: Fragment(R.layout.profilepictureselect) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val change = arguments?.getBoolean("change")?:false
        val selectImage = view.findViewById<Button>(R.id.select)
        val continueBtn = view.findViewById<Button>(R.id.contbtn)
        val image = view.findViewById<ShapeableImageView>(R.id.image)
        var Imguri:Uri?=null
        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                image.setImageURI(it)
                Imguri=it
            }
        }



        if (UserID.getUID()==null){
            //gestione errore
        }
        //se parametro -> cambio immagine -> carica da cache

        if (change){
            lifecycleScope.launch {
                image.setImageBitmap(ImageCache.get("profile${UserID.getUID()}"))

            }
        }

        continueBtn.setOnClickListener {
            //comprimo immagine
            var img=""
            if (Imguri!=null){
                img = ImageManager.compressor(requireContext(), Imguri)
                if (img==""){
                    //gestione errore
                }
            }
            //carico immagine

            lifecycleScope.launch {
                if (img!=""){
                    val res = ImageDatabase.add(img, UserID.getUID()!!, true)
                    if (res){
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()

                    }else{
                        //gestione errore
                    }
                }
            }
        }


        selectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

    }
}