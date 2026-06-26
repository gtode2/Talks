package com.example.talks.managers

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsManager(private val ctx: Context){
    private val keyTheme = stringPreferencesKey("theme")
    private val keyLang = stringPreferencesKey("language")

    suspend fun setTheme(value: String){
        ctx.dataStore.edit {
            it[keyTheme] = value
            applyTheme()
        }
    }
    suspend fun setLang(value: String){
        ctx.dataStore.edit {
            it[keyLang] = value
        }
    }


    suspend fun getLang(): String? {
        val preferences = ctx.dataStore.data.first()
        return preferences[keyLang]
    }

    suspend fun getTheme(): String? {
        val preferences = ctx.dataStore.data.first()
        return preferences[keyTheme]
    }

    fun applyTheme()= runBlocking {
        when(getTheme()){
            "light"-> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark"-> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    fun applyLang()= runBlocking {
        val lang = getLang()
        if (lang != null) {
            val loc = androidx.core.os.LocaleListCompat.forLanguageTags(lang)
            AppCompatDelegate.setApplicationLocales(loc)
        }
    }
}