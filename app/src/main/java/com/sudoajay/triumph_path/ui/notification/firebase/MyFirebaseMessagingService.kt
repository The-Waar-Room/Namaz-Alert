package com.sudoajay.triumph_path.ui.notification.firebase

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sudoajay.triumph_path.R
import com.sudoajay.triumph_path.data.proto.ProtoManager
import com.sudoajay.triumph_path.ui.mainActivity.MainActivity
import com.sudoajay.triumph_path.ui.notification.NotificationChannels
import javax.inject.Inject

class MyFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var protoManager: ProtoManager

    @Inject
    lateinit var firebaseNotification: FirebaseNotification
    private val TAG = "MyFirebaseMsgServiceTAG"


    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        val data = remoteMessage.data
        if (data.isNotEmpty() && !data.keys.contains("openUrl") && !data.keys.contains("OpenUrl")) {
            scheduleJob()
        } else {
            handleNotification(remoteMessage)
        }

        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }

    }


    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")


        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {

        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }


    private fun scheduleJob() {


    }

    private fun handleNotification(remoteMessage: RemoteMessage) {
        Log.d(TAG, "Short lived task is done.")

        val data = remoteMessage.data
        val url = if (data.keys.contains("OpenUrl")) data["OpenUrl"] else data["openUrl"]
        val notificationCompat: NotificationCompat.Builder =
            NotificationCompat.Builder(
                applicationContext,
                NotificationChannels.FireBase_PUSH_NOTIFICATION
            )
        notificationCompat.setSmallIcon(R.mipmap.ic_launcher)

        notificationCompat.setContentIntent(createPendingIntent(url.toString()))

        firebaseNotification.notifyCompat(
            remoteMessage.notification!!,
            notificationCompat
        )
    }


    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createPendingIntent(link: String): PendingIntent? {
        return if (link.isNotBlank()) {

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(link)
            PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            PendingIntent.getActivity(
                this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT
            )
        }
    }


}