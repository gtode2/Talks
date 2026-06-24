package com.example.talks.singleton

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.collection.LruCache
import com.example.talks.database.ImageDatabase
import com.example.talks.managers.ImageManager
import androidx.core.graphics.createBitmap
import com.example.talks.data.ImageData
import com.google.firebase.Timestamp


object ImageCache {
    val mem = (Runtime.getRuntime().maxMemory()/8).toInt()
    //verificare dimensione
    val cache = object: LruCache<String, ImageData>(mem){
        override fun sizeOf(key: String, value: ImageData): Int {
            return value.img.byteCount
        }
    }

    suspend fun get(id:String, isProfile:Boolean=false,  ts: Timestamp?=null):Bitmap?{
        //ritorna da cache solo se presente e ancora valida
        var prefix="image"
        if (isProfile) prefix="profile"

        val timestamp = ts ?: Timestamp.now()
        val s = "$prefix$id"

        if (cache[s]==null){
            return update(id, isProfile, timestamp)
        }else{
            //elemento esiste in cache -> Verifico TS
            if (cache[s]!!.img.height==1 && cache[s]!!.img.width==1) return null
            else{
                //immagine presente e valida
                if (isProfile) return cache[s]!!.img
                else if (ts!=null && cache[s]!!.ts==ts) return cache[s]!!.img
                else return update(id, false, timestamp)
            }
        }
    }

    suspend fun update(s:String, isProfile:Boolean=false, ts: Timestamp):Bitmap?{

        val res = ImageDatabase.get(s, isProfile)
        val pre = if (isProfile) "profile" else "image"
        val key = "$pre$s" //concatena prefix e id


        if (res==null){
            return null
        }else if (res.img==null){
            val image = ImageData(createBitmap(1,1), ts)
            cache.put(key, image)
            return null
        }else {
            val image = ImageData(res.img, ts)
            cache.put(key, image)
            return image.img
        }
    }


    suspend fun add(ctx: Context, id:String, img: Uri, isProfile: Boolean):Boolean{
        val imgStr = ImageManager.compressor(ctx, img)
        var res = ImageDatabase.add(imgStr, id, isProfile)
        if (res){
            val bmp = ImageManager.decode(imgStr)
            val imgid = if (isProfile) "profile$id" else "image$id"
            cache.put(imgid, ImageData(bmp, Timestamp.now()))
            return true
        }else{
            return false
        }
    }


    fun remove(isProfile:Boolean, postid:String=""){
        val imgid = if (isProfile) "profile$postid" else "image$postid"
        cache.remove(imgid)
    }
}