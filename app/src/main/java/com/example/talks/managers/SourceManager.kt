package com.example.talks.managers

import android.util.Log
import androidx.core.net.toUri
import org.jsoup.Jsoup
import java.net.HttpURLConnection
import java.net.URL

class SourceManager {
    companion object{
        //verifica source


        fun getFavicon(u:String): String{
            var url = u

            if (!u.startsWith("http://") && !u.startsWith("https://")){
                url = "https://"+u
            }
            var scheme = url.toUri().scheme
            var dom = url.toUri().host

            // cerco og:image
            try {
                val doc = Jsoup.connect(url).get()
                val img = doc.select("meta[property=og:image]")
                    .attr("content")
                //possibile usare doc anche per title sito / pagina
                if (img.isNotEmpty()){
                    return img
                }
            }catch (e: Exception){}

            try {
                //cerco favicon.ico
                var url = scheme+"://"+dom+"/favicon.ico"
                var res = exists(url)
                if (!res){
                    //provo con api google
                    url ="https://www.google.com/s2/favicons?domain="+dom
                    res = exists(url)
                    if (!res){
                        return "/0"
                    }
                }
                return url
            }catch (e: Exception){
                return "/1"
            }
        }

        fun exists(url:String): Boolean{
            return try{
                val conn = URL(url).openConnection() as HttpURLConnection
                conn.requestMethod="HEAD"
                conn.connectTimeout=5000
                conn.readTimeout=5000
                val res = conn.responseCode
                res in 200..399
            }catch (e: Exception){
                false
            }
        }
        fun getTitle(u:String):String? {
            try {
                val doc = Jsoup.connect(u).get()
                return doc.title()
            }catch (e: Exception){
                //gestione errore
                return null
            }

        }

    }
}