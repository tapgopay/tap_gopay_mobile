package com.example.tapgopay.remote

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.example.tapgopay.BuildConfig
import com.example.tapgopay.MainActivity
import com.example.tapgopay.utils.formatAmount
import com.example.tapgopay.utils.formatDatetime
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocketListener


class NotificationService : Service() {
    private lateinit var webSocket: okhttp3.WebSocket
    private val client = OkHttpClient()

    override fun onCreate() {
        super.onCreate()
        startForegroundServiceNotification()
        startWebsocket()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startForegroundServiceNotification() {
        val channelId = "websocket_channel"
        val channelName = "Server Notifications"
        val notificationId = 1

        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Notifications Service")
            .setContentText("Listening for notifications...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        startForeground(notificationId, notification)
    }

    private fun startWebsocket() {
        val uri = BuildConfig.REMOTE_URL.toUri()

        val request = Request.Builder()
            .url("ws://${uri.host}:${uri.port}/ws-notifications")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: okhttp3.WebSocket, response: Response) {
                Log.d(MainActivity.TAG, "Connected to notifications websocket")
            }

            override fun onMessage(webSocket: okhttp3.WebSocket, text: String) {
                try {
                    Log.d(MainActivity.TAG, "Received message from notifications websocket: $text")

                    val transaction = Gson().fromJson(text, TransactionResult::class.java)
                    showNotification(transaction)

                } catch (e: Exception) {
                    Log.e(MainActivity.TAG, "Error displaying received notification: ${e.message}")
                }
            }

            override fun onClosed(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
                Log.d(MainActivity.TAG, "Notifications websocket closed: $reason")
            }

            override fun onFailure(
                webSocket: okhttp3.WebSocket,
                t: Throwable,
                response: Response?
            ) {
                Log.e(MainActivity.TAG, "Error keeping websocket connection open: ${t.message}")
                reconnectWebSocket()
            }
        })
    }

    private fun reconnectWebSocket() {
        Handler(Looper.getMainLooper()).postDelayed({
            startWebsocket()
        }, 5000)
    }

    private fun showNotification(t: TransactionResult) {
        val sendersWalletAddress = t.sender.walletAddress.ifEmpty { "???" }
        val sendersPhoneNo = t.sender.phoneNo.ifEmpty { "+254 7xx xxx xxx " }
        val message =
            "Confirmed. ${t.transactionId} You have received KSH ${formatAmount(t.amount)} from $sendersWalletAddress, phone $sendersPhoneNo. ${
                formatDatetime(t.timestamp)
            }"

        val channelId = "websocket_channel"
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("New Message")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onDestroy() {
        webSocket.close(1000, "Service destroyed")
        super.onDestroy()
    }
}
