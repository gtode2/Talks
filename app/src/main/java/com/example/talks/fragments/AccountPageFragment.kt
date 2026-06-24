package com.example.talks.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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


        val uid = UserID.getUID()
        if (uid==null){
            parentFragmentManager.beginTransaction()
                .replace(R.id.emptyframe, LoginFragment())
                .commit()
        }
        tag.text = "@$uid"

        lifecycleScope.launch {
            val user = UserDatabase.getUser(uid!!)
            if (user.err!=null){
                logout()
                //messaggio errore tramite toast
            }else{
                name.text = "${user.name} ${user.surname}".trim()
                fw.text = user.followers.toString()
                fd.text = user.followed.toString()
            }



            val img = ImageCache.get(UserID.getUID()!!, true)
            if(img!=null){
                withContext(Dispatchers.Main){
                    profilepicture.setImageBitmap(img)
                }
            }else{
                profilepicture.setImageDrawable(null)
            }
        }

        logout.setOnClickListener{
            logout()
        }

        settings.setOnClickListener{
            val intent = Intent(requireContext(), EmptyActivity::class.java)
                .putExtra("screen", "sett")
            startActivity(intent)
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
    private fun logout(){
        FollowRepository.clear()
        BookmarkRepository.clear()
        LikeRepository.clear()
        (requireActivity() as MainActivity).logout()
    }
}