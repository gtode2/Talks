package com.example.talks.managers

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.talks.database.TagDatabase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class TagManager {

    /*
    suspend fun validate(str: String, ctx: Context):MutableList<String>{
        suspendCancellableCoroutine { cont ->
            var fList = mutableListOf<String>()
            var fTxt = ""
            val regex = Regex("@[A-Za-z0-9_]+(?:\\.[A-Za-z0-9_]+)*")
            val map = regex.findAll(str).map { it.value.removePrefix("@")}.toList()
            val total = map.size
            var ctr = 0
            var retry = false
            if (!map.isEmpty()){
                do {
                    map.forEach {
                        var err: Boolean = false
                        TagDatabase.checkUser(it) { res ->
                            //1 -> non trovato
                            //0 -> trovato
                            //-1 -> errore

                            if (res == 1) {
                                fList.add(it)
                                fTxt += "@$it\n"
                            } else if (res == -1) {
                                err = true
                            }

                            ctr++
                            if (ctr == total) {
                                //DEBUG
                                //fTxt=""
                                //err=true
                                if (fTxt != "") {
                                    fTxt += "\nVuoi modificare i tag o ignorare e pubblicare lo stesso?"
                                    AlertDialog.Builder(ctx)
                                        .setTitle("Utenti non trovati")
                                        .setMessage(fTxt)
                                        .setPositiveButton("Continua", null) //post
                                        .setNegativeButton("Modifica", null) //cancel
                                        .show()
                                } else if (err) {
                                    AlertDialog.Builder(ctx)
                                        .setTitle("Errore")
                                        .setMessage("si è verificato un errore nella verifica di alcuni tag\nVuoi riprovare, pubblicare lo stesso senza i tag che generano errore o uscire?")
                                        .setPositiveButton("Continua"){dialog,_->
                                            dialog.dismiss()
                                            cont.resume(fList)
                                        } //post
                                        .setNegativeButton("Annulla", null) //cancel
                                        .setNeutralButton("Riprova"){dialog,_->
                                            retry=true
                                            dialog.dismiss()
                                        } //retry
                                        .show()
                                }
                            }

                        }
                    }
                }while(retry)
            }


            cont.resume(mutableListOf<String>()){}
        }
        return mutableListOf<String>()

        //verifica presenza tag in testo

    }
    */




    /*
    suspend fun validate(str: String, ctx: Context):MutableList<String>{
        suspendCancellableCoroutine { cont ->
            var fList = mutableListOf<String>()
            var fTxt = ""
            val total = map.size
            var ctr = 0
            var retry = false
            if (!map.isEmpty()){
                do {
                    map.forEach {
                        var err: Boolean = false
                        TagDatabase.checkUser(it) { res ->
                            //1 -> non trovato
                            //0 -> trovato
                            //-1 -> errore

                            if (res == 1) {
                                fList.add(it)
                                fTxt += "@$it\n"
                            } else if (res == -1) {
                                err = true
                            }

                            ctr++
                            if (ctr == total) {
                                //DEBUG
                                //fTxt=""
                                //err=true
                                if (fTxt != "") {

                            }

                        }
                    }
                }while(retry)
            }


            cont.resume(mutableListOf<String>()){}
        }
        return mutableListOf<String>()

        //verifica presenza tag in testo

    }
    */



    private var error=false
    private var rt = false
    //not-found List
    private var nList=mutableListOf<String>()
    //found List
    private var fList=mutableListOf<String>()
    private var nTxt = ""


    suspend fun validate(str:String, ctx:Context): MutableList<String>{
        var confirm=false

        val regex = Regex("@[A-Za-z0-9_]+(?:\\.[A-Za-z0-9_]+)*")
        //tag List
        val tList = regex.findAll(str).map{ it.value.removePrefix("@")}.toList()
        Log.e("AAA", tList.toString())

        do {
            check(tList)
            //gestione risultati
            //se error=true
            if (error){
                AlertDialog.Builder(ctx)
                    .setTitle("Errore")
                    .setMessage("si è verificato un errore nella verifica di alcuni tag\nVuoi riprovare, pubblicare lo stesso senza i tag che generano errore o uscire?")
                    .setPositiveButton("Continua"){dialog,_->
                        dialog.dismiss()
                        confirm=true
                    } //post
                    .setNegativeButton("Annulla"){dialog,_->
                        dialog.dismiss()
                    } //cance
                    .setNeutralButton("Riprova"){dialog,_->
                        rt=true
                        dialog.dismiss()
                    } //retry
                    .show()
            }
        }while (rt)
        //se nlist non vuota
        Log.e("AAA", nList.toString())
        if (nList.isNotEmpty()){
            Log.e("AAA", "nlist non vuota")
            nTxt += "\nVuoi modificare i tag o ignorare e pubblicare lo stesso?"
            AlertDialog.Builder(ctx)
                .setTitle("Utenti non trovati")
                .setMessage(nTxt)
                .setPositiveButton("Continua"){dialog, _->
                    dialog.dismiss()
                    confirm=true
                } //post
                .setNegativeButton("Modifica"){dialog,_->
                    dialog.dismiss()
                } //cancel
                .show()
        }
        else{
            confirm = true
        }


        if (confirm){
            return fList
        }
        return mutableListOf<String>()
    }

    private suspend fun check(tList:List<String>){
        //reset error
        error=false
        tList.forEach { tag->
            Log.e("AAA", tag )
            val result = suspendCancellableCoroutine<Int> { cont->
                TagDatabase.checkUser(tag){res->
                    cont.resume(res)
                }
            }
            when(result) {
                0 -> {
                    Log.e("AAA", tag+" in flist")
                    fList.add(tag)
                }
                1 -> {
                    nList.add(tag)
                    nTxt += "@$tag\n"
                }
                -1 -> error=true
            }
        }
    }

}