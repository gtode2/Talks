package com.example.talks.data

import android.graphics.Bitmap
import com.google.firebase.Timestamp

data class ImageData (
    var img: Bitmap,
    var ts: Timestamp
)

data class ImageDataRes(
    val img:Bitmap?
)