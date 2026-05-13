package com.example.talks.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.talks.R
import androidx.recyclerview.widget.RecyclerView
import com.example.talks.data.NotificationData
import com.example.talks.singleton.ImageCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationsAdapter(
    private val notif: MutableList<NotificationData>,
    private val context: Context
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
        val typeImg = view.findViewById<ImageView>(R.id.typeimg)
        val notifblock = view.findViewById<ConstraintLayout>(R.id.notifblock)


        val notiftxt = view.findViewById<TextView>(R.id.notiftxt)

        private val scope = CoroutineScope(Dispatchers.Main.immediate)
        private var job: Job?=null

        fun bind(el:NotificationData){
            job?.cancel()
            when(el.type){
                0->{
                    typeImg.setImageResource(R.drawable.tag)
                    typeImg.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red))
                    notiftxt.text=context.getString(R.string.notiftag, el.author)
                }
                1->{
                    typeImg.setImageResource(R.drawable.comments)
                    typeImg.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue))
                    notiftxt.text=context.getString(R.string.notifcomment, el.author)
                }
                2->{
                    typeImg.setImageResource(R.drawable.person_add)
                    typeImg.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green))
                    notiftxt.text=context.getString(R.string.notiffw, el.author)
                }

            }

            job = scope.launch {
                val img = withContext(Dispatchers.IO){ ImageCache.get("profile${el.author}")}
                if (img!=null){
                    profileImg.setImageBitmap(img)
                }else{
                    profileImg.setImageDrawable(null)
                }

            }



            notifblock.setOnClickListener {
                //gestione tipo notifica
            }

        }
    }
}