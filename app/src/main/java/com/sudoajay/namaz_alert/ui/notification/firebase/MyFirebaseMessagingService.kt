package com.sudoajay.namaz_alert.ui.notification.firebase

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.data.proto.ProtoManager
import com.sudoajay.namaz_alert.ui.mainActivity.MainActivity
import com.sudoajay.namaz_alert.ui.notification.NotificationChannels
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
            scheduleJob(remoteMessage.data)
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


    private fun scheduleJob(data: MutableMap<String, String>) {


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
        Log.e(TAG, "Here open marvel ${link} ")
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