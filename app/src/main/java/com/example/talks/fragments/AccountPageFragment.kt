package com.example.talks.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.talks.EmptyActivity
import com.example.talks.MainActivity
import com.example.talks.R
import com.example.talks.database.UserDatabase
import com.example.talks.repository.BookmarkRepository
import com.example.talks.repository.FollowRepository
import com.example.talks.repository.LikeRepository
import com.example.talks.singleton.ImageCache
import com.example.talks.singleton.UserID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountPageFragment:Fragment(R.layout.userpage_lgd) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var profilepicture = view.findViewById<ImageView>(R.id.profPic)
        var name = view.findViewById<TextView>(R.id.userNS)
        var tag = view.findViewById<TextView>(R.id.userTag)
        var fw = view.findViewById<TextView>(R.id.followers)
        var fd = view.findViewById<TextView>(R.id.followed)

        var yourposts = view.findViewById<ConstraintLayout>(R.id.yourPostsBtn)
        var saved = view.findViewById<ConstraintLayout>(R.id.savedBtn)
        var settings = view.findViewById<ConstraintLayout>(R.id.settingsBtn)
        var logout = view.findViewById<ConstraintLayout>(R.id.logoutBtn)

        //getUid
        val UID = UserID.getUID()
        if (UID==null){
            //gestione errore
            //rimanda a homepage
        }
        tag.text = "@${UID}"

        var user:MutableMap<String, String>
        lifecycleScope.launch(Dispatchers.IO) {
            user = UserDatabase.getUser(UID!!)
            //verifico correttezza info ottenute
            withContext(Dispatchers.Main){
                name.text = "${user.getValue("name")} ${user.getValue("surname")}".trim()
                //trim -> se no cognome -> centra stringa
                fw.text = user.getValue("fw")
                fd.text = user.getValue("fd")

            }
            val img = ImageCache.get("profile${UserID.getUID()}")
            if(img!=null){
                withContext(Dispatchers.Main){
                    profilepicture.setImageBitmap(img)
                }
            }
        }

        logout.setOnClickListener{
            FollowRepository.clear()
            BookmarkRepository.clear()
            LikeRepository.clear()
            (requireActivity() as MainActivity).logout()
        }

        settings.setOnClickListener{
           val ft = requireActivity().supportFragmentManager.beginTransaction()
           ft.replace(R.id.frame, SettingsFragment())
               .commit()
        }

        saved.setOnClickListener{
            val intent = Intent(requireContext(), EmptyActivity::class.java)
                .putExtra("screen", "saved")
            startActivity(intent)
        }
        yourposts.setOnClickListener{
            val intent = Intent(requireContext(),EmptyActivity::class.java)
                .putExtra("screen","your")
            startActivity(intent)
        }
    }
}