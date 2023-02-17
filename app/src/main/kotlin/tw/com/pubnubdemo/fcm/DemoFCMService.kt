package tw.com.pubnubdemo.fcm

import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import tw.com.pubnubdemo.R
import tw.com.pubnubdemo.data.PreferencesDataStore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class DemoFCMService : FirebaseMessagingService() {

    @Inject
    lateinit var dataStore: PreferencesDataStore

    private val tag = this::class.simpleName

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(tag, throwable.message.toString())
    }
    private val serviceScope =
        CoroutineScope(Dispatchers.Default + coroutineExceptionHandler)

    override fun onCreate() {
        Log.d(tag, "FCM created!")
        super.onCreate()
    }

    override fun onNewToken(token: String) {

        serviceScope.launch {
            dataStore.setFCMToken(token)
            Log.d(tag, "FCM token stored!")
        }

        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(tag, "messageFrom: ${message.from}")
        Log.d(tag, "message: $message")
        message.notification?.let { notification ->
            showNotification(mapOf(
                "title" to notification.title,
                "body" to notification.body
            ))
        }
        super.onMessageReceived(message)
    }

    private fun showNotification(data: Map<String, String?>) {
        val notificationBuilder =
            NotificationCompat.Builder(this, "demo_channel")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(data["title"])
                .setContentText(data["body"])
        val manager = NotificationManagerCompat.from(this)
        manager.notify(123, notificationBuilder.build())
    }

    override fun onDestroy() {
        serviceScope.cancel()
        Log.d(tag, "FirebaseCloudMessage died!")
        super.onDestroy()
    }

}