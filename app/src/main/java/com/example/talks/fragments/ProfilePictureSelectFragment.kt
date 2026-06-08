package com.example.talks.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfilePictureSelectFragment: Fragment(R.layout.profilepictureselect) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val change = arguments?.getBoolean("change")?:false
        val selectImage = view.findViewById<LinearLayout>(R.id.select)
        val continueBtn = view.findViewById<LinearLayout>(R.id.contbtn)
        val image = view.findViewById<ShapeableImageView>(R.id.image)
        var Imguri:Uri?=null
        var wasEmpty = false
        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                image.setImageURI(it)
                Imguri=it
            }
        }



        if (UserID.getUID()==null){
            Toast.makeText(requireContext(), getString(R.string.errReLog), Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }
        //se parametro -> cambio immagine -> carica da cache


        lifecycleScope.launch {
            val oldImg = withContext(Dispatchers.IO){ImageCache.get("profile${UserID.getUID()}")}
            if (oldImg==null){
                wasEmpty=true
            }

            if (change){
                lifecycleScope.launch {
                    image.setImageBitmap(oldImg)
                }
            }
        }




        continueBtn.setOnClickListener {
            if (Imguri!=null){
                lifecycleScope.launch {
                    val res = ImageCache.add(requireContext(), UserID.getUID()!!, Imguri, true)

                    if (res){
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }else{
                        Toast.makeText(requireContext(), getString(R.string.errImg), Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                if (!wasEmpty){
                    //prima immagine c'era, adesso no
                    lifecycleScope.launch {
                        val res = withContext(Dispatchers.IO){ImageCache.remove(true)}
                        if (res){
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        }else{
                            Toast.makeText(requireContext(), getString(R.string.errImgRem), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }


        selectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

    }
}