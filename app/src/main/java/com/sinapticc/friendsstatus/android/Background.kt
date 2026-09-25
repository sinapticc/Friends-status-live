package com.sinapticc.friendsstatus.android

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sinapticc.friendsstatus.BuildConfig
import com.sinapticc.friendsstatus.data.Api
import com.sinapticc.friendsstatus.data.ApiException
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.concurrent.TimeUnit

/** Shared preferences file used by the app, the worker and the widget. */
fun prefs(ctx: Context) = ctx.getSharedPreferences("fsl", Context.MODE_PRIVATE)

/** Lets a running app react to pushes right away. */
object LiveBus {
    val changes = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
}

/** Firebase is set up from BuildConfig, so a build without google-services.json simply has no push. */
object Push {
    fun enabled(ctx: Context): Boolean {
        if (BuildConfig.FIREBASE_APP_ID.isBlank()) return false
        if (FirebaseApp.getApps(ctx).isEmpty()) {
            FirebaseApp.initializeApp(
                ctx,
                FirebaseOptions.Builder()
                    .setApplicationId(BuildConfig.FIREBASE_APP_ID)
                    .setApiKey(BuildConfig.FIREBASE_API_KEY)
                    .setProjectId(BuildConfig.FIREBASE_PROJECT_ID)
                    .setGcmSenderId(BuildConfig.FIREBASE_SENDER_ID)
                    .build(),
            )
        }
        return true
    }

    fun token(ctx: Context, onToken: (String?) -> Unit) {
        if (!enabled(ctx)) return onToken(null)
        runCatching {
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { onToken(it) }
                .addOnFailureListener { onToken(null) }
        }.onFailure { onToken(null) }
    }
}

/** Receives the server's silent "something changed" messages. */
class FslMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        LiveBus.changes.tryEmit(Unit)
        Sync.now(this)
    }

    override fun onNewToken(token: String) {
        prefs(this).edit().putString("pushPending", token).apply()
        Sync.now(this)
    }
}

object Sync {
    /** Every 15 minutes (Android's minimum), even when the app is closed. */
    fun schedule(ctx: Context) {
        if (BuildConfig.API_URL.isBlank()) return
        val req = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()
        WorkManager.getInstance(ctx).enqueueUniquePeriodicWork("sync", ExistingPeriodicWorkPolicy.KEEP, req)
    }

    /** Right away, e.g. after a push. */
    fun now(ctx: Context) {
        if (BuildConfig.API_URL.isBlank()) return
        // Not expedited: that needs a foreground notification on Android 11 and older.
        val req = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()
        WorkManager.getInstance(ctx).enqueueUniqueWork("sync-now", ExistingWorkPolicy.REPLACE, req)
    }
}

/** Fetches the feed in the background and refreshes the home screen widgets. */
class SyncWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val p = prefs(applicationContext)
        val token = p.getString("token", null) ?: return Result.success()
        val api = Api(BuildConfig.API_URL) { token }
        return try {
            p.getString("pushPending", null)?.let { t ->
                api.patchMe("fcmToken" to t)
                p.edit().putString("pushSent", t).remove("pushPending").apply()
            }
            WidgetData.save(applicationContext, api.feed(System.currentTimeMillis()))
            FslWidget.refreshAll(applicationContext)
            Result.success()
        } catch (e: ApiException) {
            if (e.status == 401) Result.success() else Result.retry()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
