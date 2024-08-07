package com.sudoajay.triumph_path.ui.notificationSound.repository

import android.content.Context
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sudoajay.triumph_path.R
import com.sudoajay.triumph_path.databinding.HolderSelectNotificationSoundBinding
import javax.inject.Inject


class SelectNotificationSoundAdapter @Inject constructor(private var context: Context) :
    RecyclerView.Adapter<SelectNotificationSoundAdapter.ViewHolder>() {

    var notificationSoundList = mutableListOf<String>()
    var notificationSoundValue = mutableListOf<String>()
    var selectedNotificationSound = 0
    var mp: MediaPlayer? = null
    var ringtone: Ringtone? = null

    inner class ViewHolder(
        val binding: HolderSelectNotificationSoundBinding? = null,
        val context: Context
    ) : RecyclerView.ViewHolder(binding?.root!!) {
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            binding = HolderSelectNotificationSoundBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            context = parent.context
        )

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val text = notificationSoundList[position]
        val value = notificationSoundValue[position]

        viewHolder.binding!!.selectNotificationSoundTextView.text = value

        viewHolder.binding.tickImageView.visibility =
            if (text.toInt() == selectedNotificationSound) View.VISIBLE else View.GONE

        viewHolder.binding.mainConstraintLayout.setOnClickListener {
            selectedNotificationSound = text.toInt()
            mp?.stop()
            ringtone?.stop()
            when (text) {
                context.resources.getStringArray(R.array.setNotificationSound)[1] -> {
                    mp = MediaPlayer.create(context, R.raw.azan_in_islam)
                    mp?.start()
                }
                else -> {
                    val notification: Uri =
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    ringtone = RingtoneManager.getRingtone(context, notification)
                    ringtone?.play()
                }
            }
            notifyItemRangeChanged(0, notificationSoundList.size)
        }


        mp?.setOnCompletionListener { mp -> // TODO Auto-generated method stub
            mp!!.reset()
            mp.release()
        }

    }


    override fun getItemCount() = notificationSoundList.size

}