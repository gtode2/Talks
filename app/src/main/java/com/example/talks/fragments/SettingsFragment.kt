package com.example.talks.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.addCallback

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.talks.R
import com.example.talks.managers.SettingsManager
import kotlinx.coroutines.launch
import java.util.Locale

class SettingsFragment:Fragment(R.layout.settings) {

    private lateinit var settingsManager: SettingsManager
    private lateinit var btnIt:TextView
    private lateinit var btnEn:TextView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsManager = SettingsManager(requireContext())
        settingsManager.applyLang()

        var back = view.findViewById<Button>(R.id.settcancbtn)
        back.setOnClickListener{
            //ricreare activity -> intent = sett
            requireActivity().intent.putExtra("From", "sett")
            requireActivity().recreate()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            //ricreare activity -> intent = sett
            requireActivity().intent.putExtra("From", "sett")
            requireActivity().recreate()
        }

        btnIt = view.findViewById(R.id.btnIt)
        btnEn = view.findViewById(R.id.btnEn)
        val langtv = view.findViewById<TextView>(R.id.languagetv)




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
    }
}