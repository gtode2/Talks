package com.example.talks.managers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.core.graphics.scale
import java.io.ByteArrayOutputStream

class ImageManager {
    companion object{
        fun compressor(ctx:Context, uri:Uri):String{
            var tl = false
            var size=800
            var bitmap: Bitmap = toBitmap(ctx, uri)
            var b64Str=""
            do {
                tl=false
                var redbitmap = reduce(bitmap, size)
                var c = compress(redbitmap)
                if (c.size>730_000){
                    if (size<600){
                        //impossible scalare
                        tl=false
                        return ""
                        //errore->impossibile comprimere

                    }else{
                        tl=true
                        size-=100
                    }
                }
                b64Str= Base64.encodeToString(c, Base64.NO_WRAP)
            }while (tl)
            return b64Str
        }
        private fun toBitmap(ctx: Context,uri: Uri): Bitmap{
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.P){
                val src = ImageDecoder.createSource(ctx.contentResolver, uri)
                return ImageDecoder.decodeBitmap(src)
            }else{
                return MediaStore.Images.Media.getBitmap(ctx.contentResolver, uri)
            }
        }
        private fun reduce(bmp:Bitmap, min:Int=800):Bitmap{
            if (bmp.width<=0 ||  bmp.height<=0){
                throw IllegalArgumentException("Size must be positive")
            }
            val ratio = minOf(min.toFloat()/bmp.width, min.toFloat()/bmp.height)
            if (ratio>=1f) return bmp
            val width = bmp.width*ratio
            val height = bmp.height*ratio
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.P){
                return bmp.scale(width.toInt(), height.toInt(), true)
            }else{
                return Bitmap.createScaledBitmap(bmp, width.toInt(), height.toInt(), true)
            }
        }
        private fun compress(bmp:Bitmap):ByteArray{
            val stream = ByteArrayOutputStream()
            val format = if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.R) Bitmap.CompressFormat.WEBP_LOSSY else Bitmap.CompressFormat.JPEG
            var qual = 85
            do {
                stream.reset()
                bmp.compress(format, qual, stream)
                qual-=5
            }while (stream.size()>730_000 && qual>10)
            return stream.toByteArray()
        }


        fun decode(str:String):Bitmap {
            val img = Base64.decode(str, Base64.NO_WRAP)
            return BitmapFactory.decodeByteArray(img, 0, img.size)
        }

    }
}