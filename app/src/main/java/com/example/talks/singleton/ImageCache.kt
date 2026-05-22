package com.example.talks.singleton

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.collection.LruCache
import com.example.talks.database.ImageDatabase
import com.example.talks.managers.ImageManager
import androidx.core.graphics.createBitmap
import com.example.talks.data.PostData
import com.example.talks.database.PostDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
                pr=false
            }
            val b64 = ImageDatabase.get(id, pr)

            if (b64==null){
                //bmp placeholder -> evita chiamata inutile a db
                cache.put(s, createBitmap(1, 1))
                return null
            }else{
                val bmp = ImageManager.decode(b64)
                cache.put(s,bmp)
                return bmp
            }

        }
    }

    suspend fun add(ctx: Context, id:String, img: Uri, isProfile: Boolean):Boolean{
        //gestire in dispatchers IO

        val imgStr = ImageManager.compressor(ctx, img)
        var res = ImageDatabase.add(imgStr, id, isProfile)
        if (res){
            val bmp = ImageManager.decode(imgStr)
            val imgid = if (isProfile) "profile$id" else "image$id"
            cache.put(imgid, bmp)
            return true
        }else{
            return false
        }
    }

    fun remove(id:String, isProfile:Boolean){
        //rimuove da cache
        //se profilo -> rimuovi anche  da db

    }
}