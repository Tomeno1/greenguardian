/*package data

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.firebase.messaging.FirebaseMessaging

class MyApp : Application() {
    companion object {
        const val CHANNEL_ID = "GreenGuardian"
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                println("Token no fue generadooo")
                return@addOnCompleteListener
            }
            val token = it.result
            println("Token: $token")
        }
        createNotificationChanel()
    }

    private fun createNotificationChanel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID,
                "GreenGuardian Notificaciones",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Estas notificaciones son de fcm"
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
*/