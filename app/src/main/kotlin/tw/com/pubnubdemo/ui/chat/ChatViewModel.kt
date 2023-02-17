package tw.com.pubnubdemo.ui.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import tw.com.pubnubdemo.BuildConfig
import tw.com.pubnubdemo.data.PreferencesDataStore
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.api.UserId
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.enums.PNPushType
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import com.pubnub.api.models.consumer.push.payload.PushPayloadHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val dataStore: PreferencesDataStore) : ViewModel() {

    private val TAG = "ChatViewModel"

    private val pubNub: PubNub

    private val _msgList = MutableStateFlow(listOf<String>())
    val msgList = _msgList.asStateFlow()

    private val fcmToken get() = dataStore.getFCMToken()

    init {
        val pnConfig = PNConfiguration(UserId("defaultUserName")).apply {
            subscribeKey = BuildConfig.PN_SUB_KEY
            publishKey = BuildConfig.PN_PUB_KEY
            secretKey = BuildConfig.PN_SEC_KEY
        }
        pubNub = PubNub(pnConfig)

        pubNub.addListener(object : SubscribeCallback() {
            override fun status(pubnub: PubNub, pnStatus: PNStatus) {
            }

            override fun presence(pubnub: PubNub, pnPresenceEventResult: PNPresenceEventResult) {
//                println("Presence event: ${pnPresenceEventResult.event}")
//                println("Presence channel: ${pnPresenceEventResult.channel}")
//                println("Presence uuid: ${pnPresenceEventResult.uuid}")
//                println("Presence timetoken: ${pnPresenceEventResult.timetoken}")
//                println("Presence occupancy: ${pnPresenceEventResult.occupancy}")
            }

            override fun message(pubnub: PubNub, pnMessageResult: PNMessageResult) {
                Log.d(
                    TAG,
                    "Received msg:[${pnMessageResult.message}] from ${pnMessageResult.publisher}"
                )
                _msgList.value += pnMessageResult.message.toString()

//                msgList.add("${pnMessageResult.publisher}: ${pnMessageResult.message}")
//                println("Message payload: ${pnMessageResult.message}")
//                println("Message channel: ${pnMessageResult.channel}")
//                println("Message publisher: ${pnMessageResult.publisher}")
//                println("Message timetoken: ${pnMessageResult.timetoken}")
            }
        })
    }

    fun setUserId(newUserId: String?) {
        if (newUserId != null) pubNub.configuration.userId = UserId(newUserId)
    }

    fun setChannel(channelList: List<String>) {
        pubNub.subscribe(channelList)
        fcmToken?.let { token ->
            pubNub.addPushNotificationsOnChannels(
                pushType = PNPushType.FCM,
                deviceId = token,
                channels = channelList
            ).async { _, status ->
                if (status.error) Log.e(TAG, status.exception?.message.toString())
                else {
                    Log.d(TAG, "FCM token added! fcm_token:$token")
                    Log.d(TAG, "FCM token added at ${status.affectedChannels.joinToString()}")
                }
            }
        }
    }

    fun publish(channelId: String, message: String) {
        pubNub.publish(channel = channelId, message = generateMessagePayload(message))
            .async { _, status ->
                if (status.error) {
                    Log.e(
                        TAG,
                        "error occurred when sending, errorMsg: ${status.exception?.message}"
                    )
                } else {
                    Log.d(TAG, "send message succeed!")
                }
            }
    }

    private fun generateMessagePayload(message: String): Map<String, Any> {

        val pushPayloadHelper = PushPayloadHelper().apply {
            commonPayload = buildMap {
                put("text", message)
                if (BuildConfig.DEBUG) put("pn_debug", true)
            }
            fcmPayload = PushPayloadHelper.FCMPayload().apply {
                notification = PushPayloadHelper.FCMPayload.Notification().apply {
                    title = "${pubNub.configuration.userId.value} send a message"
                    body = message
                }
                data = buildMap {
                    put("action", "OPEN_A_PAGE")
                    put("username", pubNub.configuration.userId.value)
                }
                custom = mapOf()
            }
        }
        return pushPayloadHelper.build()
    }

    override fun onCleared() {
        fcmToken?.let { token ->
            pubNub.removeAllPushNotificationsFromDeviceWithPushToken(
                pushType = PNPushType.FCM,
                deviceId = token
            )
            Log.d(TAG, "FCM token removed! fcm_token:$token")
        }
        pubNub.unsubscribeAll()
        pubNub.disconnect()
        pubNub.destroy()
        Log.d(TAG, "pubNub destroyed!")
        super.onCleared()
    }

}