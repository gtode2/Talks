package com.example.talks.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.talks.R


class AccountCreationFragment: Fragment(R.layout.accountcreation) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cont = view.findViewById<Button>(R.id.cont)

        cont.setOnClickListener {
            //TEMP - goto profile picture select
            parentFragmentManager.beginTransaction()
                .replace(R.id.emptyframe, ProfilePictureSelectFragment())
                .commit()
        }

    }
}
