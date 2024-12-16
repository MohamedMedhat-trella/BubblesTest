package com.trella.bubblestest

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.trella.bubblestest.ui.theme.BubblesTestTheme

private const val CHANNEL_ID = "123"
private const val NOTIFICATION_ID = 24
private const val SHORTCUT_ID = "shortcut-1"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BubblesTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        Button(
                            onClick = {
                                createShortcut()
                            }
                        ) {
                            Text(text = "Create Shortcut")
                        }

                        Button(
                            onClick = {
                                createBubble()
                            }
                        ) {
                            Text(text = "Show Bubble")
                        }

                        Button(
                            onClick = {
                                startActivity(Intent(this@MainActivity, BubbleActivity::class.java))
                            }
                        ) {
                            Text(text = "Open target activity")
                        }
                    }
                }
            }
        }
    }

    private fun createShortcut() {
        val shortcut = ShortcutInfoCompat.Builder(applicationContext, SHORTCUT_ID)
            .setShortLabel("Open bubble activity")
            .setLongLabel("Opens the bubble activity when clicked")
            .setIcon(IconCompat.createWithResource(applicationContext, R.drawable.baseline_360_24))
            .setIntent(Intent(Intent.ACTION_DEFAULT))
            .setLongLived(true)
            .build()

        ShortcutManagerCompat.pushDynamicShortcut(applicationContext, shortcut)
    }

    private fun createBubble() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create bubble intent
        val target = Intent(applicationContext, BubbleActivity::class.java)
        val bubbleIntent =
            PendingIntent.getActivity(applicationContext, 0, target, FLAG_MUTABLE /* flags */)


        // Create bubble metadata
        val bubbleData =
            NotificationCompat.BubbleMetadata.Builder(
                bubbleIntent,
                IconCompat.createWithResource(applicationContext, R.drawable.baseline_5g_24)
            ).setDesiredHeight(600)
                .setIcon(IconCompat.createWithResource(this, R.drawable.baseline_1x_mobiledata_24))
                .setSuppressNotification(true)
                .build()

        val chatBot: Person = Person.Builder()
            .setBot(true)
            .setName("BubbleBot")
            .setImportant(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Mohamed Medhat",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    setAllowBubbles(true)
                }
            }
            notificationManager.createNotificationChannel(channel)
        }

        val personA = Person.Builder()
            .setIcon(
                IconCompat.createWithResource(
                    applicationContext,
                    R.drawable.baseline_accessibility_24
                )
            )
            .setName("userName").build()
        val messagingStyle = NotificationCompat.MessagingStyle(personA)


        val builder =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Your to-do list is here")
                .setContentText("Click to open to-do list")
                .setContentIntent(bubbleIntent)
                .setSmallIcon(R.drawable.baseline_30fps_24)
                .setBubbleMetadata(bubbleData)
                .addPerson(chatBot)
                .setOngoing(true)
                .setShortcutId(SHORTCUT_ID)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setStyle(messagingStyle)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}
