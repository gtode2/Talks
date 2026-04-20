package com.example.talks.fragments

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.talks.R
import com.google.android.material.imageview.ShapeableImageView

class ProfilePictureSelectFragment: Fragment(R.layout.profilepictureselect) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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


        selectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

    }
}