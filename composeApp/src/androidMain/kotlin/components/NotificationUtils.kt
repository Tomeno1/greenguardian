// androidMain/kotlin/components/NotificationUtils.kt
package components

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import techminds.greenguardian.MainActivity

 actual fun sendLocalNotification(context: Any, title: String, message: String, notificationId: Int) {
    val androidContext = context as Context

    // Configurar el Intent para cuando se haga clic en la notificación
    val intent = Intent(androidContext, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.getActivity(
            androidContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    } else {
        PendingIntent.getActivity(
            androidContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    // Crear el canal de notificación si es necesario
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelId = "sensor_alert_channel"
        val channelName = "Sensor Alerts"
        val channelDescription = "Notificaciones para alertas de sensores"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }

        val notificationManager: NotificationManager =
            androidContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    // Crear la notificación
    val builder = NotificationCompat.Builder(androidContext, "sensor_alert_channel")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    // Enviar la notificación
    with(NotificationManagerCompat.from(androidContext)) {
        // Comprobar si se tiene el permiso para enviar notificaciones
        if (ContextCompat.checkSelfPermission(
                androidContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Solicitar permiso si no se tiene
            ActivityCompat.requestPermissions(
                (androidContext as MainActivity),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1001 // Un código de solicitud arbitrario
            )
            return
        }

        // Si el permiso está concedido, enviar la notificación
        notify(notificationId, builder.build())
    }
}
