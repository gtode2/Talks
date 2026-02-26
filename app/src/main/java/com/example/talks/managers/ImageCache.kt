package com.example.talks.managers

import android.graphics.Bitmap
import androidx.collection.LruCache

class ImageCache(size:Int) {
    private val cache = object: LruCache<String, Bitmap>(size){}
    fun get(str:String): Bitmap? {
        return cache.get(str)
    }
    fun add(str:String, bmp:Bitmap){
        cache.put(str,bmp)
    }

}