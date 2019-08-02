package io.neolution.eventify.Services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.neolution.eventify.R
import io.neolution.eventify.Utils.AppUtils
import io.neolution.eventify.View.Activities.HomeActivity
import kotlin.random.Random


class MessagingService: FirebaseMessagingService() {

    private lateinit var notificationManager: NotificationManager
    private val CHANNEL_ID = "Tukio"

    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)

    }

    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)

        val handler = Handler(Looper.getMainLooper())
        handler.post{
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                setupNotificationChannels()
            }

            val notificationId = Random.nextInt(6000)
            notificationManager.notify(notificationId, getNotificationBuilder().build())
        }

    }



    private fun getNotificationBuilder(): NotificationCompat.Builder{
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        return  NotificationCompat.Builder(this, AppUtils.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.tukio_logo_t)
            .setContentTitle("New Event Added!")
            .setContentText("Click to find out more..")
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(setupPendingIntent())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupNotificationChannels(){

        val channelName = getString(R.string.channel_name)
        val channelDescription = getString(R.string.channel_desc)

        val adminChannel : NotificationChannel
        adminChannel = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_LOW)
        adminChannel.description = channelDescription
        adminChannel.importance = NotificationManager.IMPORTANCE_MIN
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.YELLOW
        adminChannel.enableVibration(true)

        notificationManager.createNotificationChannel(adminChannel)

    }

    private fun setupPendingIntent(): PendingIntent{

        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        return PendingIntent.getActivity(this, 0 , intent,
            PendingIntent.FLAG_ONE_SHOT)
    }

}