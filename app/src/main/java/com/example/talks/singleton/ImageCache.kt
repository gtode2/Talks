package com.example.talks.singleton

import android.graphics.Bitmap
import android.util.Log
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
            Log.e("AAA", "cache hit ${s} ", )
            if (cache[s]!!.height==1 &&cache[s]!!.width==1){
                Log.e("AAA", "no img ${s} ", )
                return null
            }else{
                return cache[s]
            }
        }else{
            Log.e("AAA", "cache miss ${s} ", )
            //cerca da firebase
            var id: String
            var pr:Boolean = false
            if (s.startsWith("profile")){
                id = s.removePrefix("profile")
                pr=true
            }else{
                id = s.removePrefix("image")
                pr=false
            }
            val b64 = ImageDatabase.get(id, pr)

            if (b64==null){
                Log.e("AAA", "db miss ${s} ", )
                //bmp placeholder -> evita chiamata inutile a db
                cache.put(s, createBitmap(1, 1))
                return null
            }else{
                Log.e("AAA", "db hit ${s} ", )
                val bmp = ImageManager.decode(b64)
                cache.put(s,bmp)
                return bmp
            }

        }
    }
}