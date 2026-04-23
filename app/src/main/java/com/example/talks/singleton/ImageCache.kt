package com.example.talks.singleton

import android.graphics.Bitmap
import androidx.collection.LruCache
import com.example.talks.database.ImageDatabase
import com.example.talks.managers.ImageManager
import androidx.core.graphics.createBitmap

object ImageCache {
    val mem = (Runtime.getRuntime().maxMemory()/8).toInt()
    val cache = object: LruCache<String, Bitmap>(mem){
        override fun sizeOf(key: String, value: Bitmap): Int {
            return value.byteCount
        }
    }
    suspend fun get(s:String):Bitmap?{
        // se non esiste -> carica da ImageDatabase
        if (cache[s]!=null){
            if (cache[s]!!.height==1 &&cache[s]!!.width==1){
                return null
            }else{
                return cache[s]
            }
        }else{
            //cerca da firebase
            var id: String
            var pr:Boolean = false
            if (s.startsWith("profile")){
                id = s.removePrefix("profile")
                pr=true
            }else{
                id = s.removePrefix("image")
            }
            val b64 = ImageDatabase.get(id, pr)

            if (b64==null){
                //bmp placeholder -> evita chiamata inutile a db
                cache.put(id, createBitmap(1, 1))
                return null
            }else{
                val bmp = ImageManager.decode(b64)
                cache.put(id,bmp)
                return bmp
            }

        }
    }
}