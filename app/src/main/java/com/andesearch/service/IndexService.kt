package com.andesearch.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.andesearch.MainActivity
import com.andesearch.domain.index.IndexEngine
import com.andesearch.domain.model.IndexStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class IndexService : Service() {

    @Inject lateinit var indexEngine: IndexEngine

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var isIndexing = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildNotification("准备中…")
        startForeground(NOTIFICATION_ID, notification)

        if (!isIndexing) {
            startIndexing()
        }

        return START_STICKY
    }

    private fun startIndexing() {
        isIndexing = true
        scope.launch {
            indexEngine.status.collectLatest { status ->
                updateNotification(status)
                if (status is IndexStatus.Complete || status is IndexStatus.Error) {
                    stopForeground(STOP_FOREGROUND_DETACH)
                    stopSelf()
                }
            }
        }

        indexEngine.startIndexing()
    }

    private fun updateNotification(status: IndexStatus) {
        val text = when (status) {
            is IndexStatus.Idle -> "空闲"
            is IndexStatus.Scanning -> "已索引 ${status.current} 个文件"
            is IndexStatus.Building -> status.phase
            is IndexStatus.Complete -> "索引完成"
            is IndexStatus.Error -> "错误: ${status.message}"
        }
        val notification = buildNotification(text)
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(text: String) = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("文件索引中")
        .setContentText(text)
        .setSmallIcon(android.R.drawable.ic_menu_search)
        .setOngoing(true)
        .setContentIntent(
            PendingIntent.getActivity(
                this, 0,
                Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
        .build()

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "索引服务",
            NotificationManager.IMPORTANCE_LOW
        )
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "index_service"
        private const val NOTIFICATION_ID = 1
    }
}
