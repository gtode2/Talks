package com.example.talks.fragments

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat.recreate

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.talks.EmptyActivity
import com.example.talks.MainActivity
import com.example.talks.PostCreationActivity
import com.example.talks.R
import com.example.talks.managers.SettingsManager
import com.example.talks.singleton.UserID
import kotlinx.coroutines.launch
import java.util.Locale

class SettingsFragment:Fragment(R.layout.settings) {

    private lateinit var settingsManager: SettingsManager
    private lateinit var btnIt:TextView
    private lateinit var btnEn:TextView
    private lateinit var btnDk: ImageView
    private lateinit var btnLt: ImageView



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsManager = SettingsManager(requireContext())
        settingsManager.applyLang()


        val profilePicture = view.findViewById<LinearLayout>(R.id.profilePicture)

        if (UserID.getUID()==null){

            profilePicture.alpha=0.5f
            profilePicture.isEnabled=false
        }


        profilePicture.setOnClickListener {
            val intent = Intent(requireContext(), EmptyActivity::class.java)
                .putExtra("screen", "pps")
                .putExtra("prpicChange", true)
            startActivity(intent)
        }



        var back = view.findViewById<ImageView>(R.id.close)
        back.setOnClickListener{
            val intent = Intent(requireContext(), MainActivity::class.java)
                .putExtra("From", "user")
            startActivity(intent)
            requireActivity().finish()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val intent = Intent(requireContext(), MainActivity::class.java)
                .putExtra("From", "user")
            startActivity(intent)
            requireActivity().finish()
        }

        btnIt = view.findViewById(R.id.btnIt)
        btnEn = view.findViewById(R.id.btnEn)
        val langtv = view.findViewById<TextView>(R.id.languagetv)

        btnLt = view.findViewById(R.id.btnLt)
        btnDk = view.findViewById(R.id.btnDk)


        viewLifecycleOwner.lifecycleScope.launch {
            if (!isAdded) return@launch

            var lang = settingsManager.getLang()
            if (lang==null) {
                //settings = null -> prendo default
                lang = Locale.getDefault().language
            }
            if (lang=="it"){
                //imposto it
                btnIt.isSelected=true
                btnEn.isSelected=false
            }else{
                //lingua di default = en
                btnIt.isSelected=false
                btnEn.isSelected=true
            }

            var theme: String? = settingsManager.getTheme()
            if (theme==null){
                theme = defaultTheme(requireContext())
            }
            if (theme=="light"){
                btnLt.isSelected=true
                btnDk.isSelected=false
            }else{
                btnLt.isSelected=false
                btnDk.isSelected=true
            }

        }




        btnIt.setOnClickListener {
            //set ita
            btnIt.isSelected=true
            btnEn.isSelected=false
            viewLifecycleOwner.lifecycleScope.launch {
                //if (!isAdded) return@launch
                settingsManager.setLang("it")
                langtv.text="Lingua"

            }
        }
        btnEn.setOnClickListener {
            //set en
            btnIt.isSelected=false
            btnEn.isSelected=true
            viewLifecycleOwner.lifecycleScope.launch {
                //if (!isAdded) return@launch
                settingsManager.setLang("en")
                //modificare stringhe
                langtv.text="Language"
            }
        }

        btnLt.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                settingsManager.setTheme("light")
                requireActivity().intent.putExtra("From", "sett")
                requireActivity().recreate()
                //passo parametro a activity -> from settings
            }
        }
        btnDk.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                settingsManager.setTheme("dark")
                requireActivity().intent.putExtra("From", "sett")
                requireActivity().recreate()
            }
        }
    }
    fun defaultTheme(context: Context): String {
        return when (context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> "dark"
            else -> "light"
        }
    }
}