// commonMain/kotlin/components/NotificationUtils.kt
package components

expect fun sendLocalNotification(context: Any, title: String, message: String, notificationId: Int)