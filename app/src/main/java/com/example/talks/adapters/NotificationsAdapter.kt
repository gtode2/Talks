package com.example.talks.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.talks.R
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.data.NotificationData

class NotificationsAdapter(
    private val notif: MutableList<NotificationData>,
): RecyclerView.Adapter<NotificationsAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context)
            .inflate(R.layout.notificationblock, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notif[position])
    }

    override fun getItemCount(): Int=notif.size

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val profileImg = view.findViewById<ImageView>(R.id.profileimg)
        val notiftxt = view.findViewById<TextView>(R.id.notiftxt)

        fun bind(el:NotificationData){
            //gestione type, onclick, ...

            notiftxt.text=el.post

        }
    }
}