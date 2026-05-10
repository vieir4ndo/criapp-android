package com.example.criapp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.criapp.NotificationActivity
import com.example.criapp.R
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.RetrofitProvider
import com.example.criapp.data.repository.NotificationRepositoryImpl
import com.example.criapp.models.responses.NotificationResponse

class NotificationWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val CHANNEL_ID = "criapp_notifications"
    }

    override suspend fun doWork(): Result {
        val prefsManager = PreferencesManager(context)

        if (!prefsManager.isSessionActive()) return Result.success()

        val apiService = RetrofitProvider.create(prefsManager)
        val repository = NotificationRepositoryImpl(apiService)

        val result = repository.getPendingNotifications()
        if (result.isFailure) return Result.retry()

        val notifications = result.getOrDefault(emptyList())
        createNotificationChannel()

        notifications.forEach { notification ->
            showSystemNotification(notification)
            repository.markAsDelivered(notification.id)
        }

        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notification_channel_description)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun showSystemNotification(notification: NotificationResponse) {
        val title = notification.title.ifBlank { context.getString(R.string.app_name) }
        val body = notification.body.ifBlank { context.getString(R.string.notifications) }

        val intent = Intent(context, NotificationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notification.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.notifications)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context)
                .notify(notification.id.hashCode(), builder.build())
        } catch (e: SecurityException) {
            // POST_NOTIFICATIONS permission not granted on Android 13+
        }
    }
}
