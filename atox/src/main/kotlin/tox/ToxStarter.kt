package ltd.evilcorp.atox.tox

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import im.tox.tox4j.core.exceptions.ToxNewException
import javax.inject.Inject
import ltd.evilcorp.atox.ToxService
import ltd.evilcorp.domain.feature.UserManager
import ltd.evilcorp.domain.tox.PublicKey
import ltd.evilcorp.domain.tox.SaveManager
import ltd.evilcorp.domain.tox.SaveOptions
import ltd.evilcorp.domain.tox.Tox
import ltd.evilcorp.domain.tox.ToxAvEventListener
import ltd.evilcorp.domain.tox.ToxEventListener

private const val TAG = "ToxStarter"

class ToxStarter @Inject constructor(
    private val saveManager: SaveManager,
    private val userManager: UserManager,
    private val listenerCallbacks: EventListenerCallbacks,
    private val tox: Tox,
    private val eventListener: ToxEventListener,
    private val avEventListener: ToxAvEventListener,
    private val context: Context,
    private val preferences: SharedPreferences
) {
    fun startTox(save: ByteArray? = null): Boolean = try {
        listenerCallbacks.setUp(eventListener)
        listenerCallbacks.setUp(avEventListener)
        tox.start(
            SaveOptions(
                save,
                udpEnabled = preferences.getBoolean("udp_enabled", false)
            ),
            eventListener, avEventListener
        )
        startService()
        true
    } catch (e: ToxNewException) {
        Log.e(TAG, e.message)
        false
    }

    fun stopTox() = context.run {
        stopService(Intent(this, ToxService::class.java))
    }

    fun tryLoadTox(): Boolean {
        tryLoadSave()?.also { save ->
            startTox(save)
            userManager.verifyExists(tox.publicKey)
            return true
        }
        return false
    }

    private fun startService() = context.run {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            startService(Intent(this, ToxService::class.java))
        } else {
            startForegroundService(Intent(this, ToxService::class.java))
        }
    }

    private fun tryLoadSave(): ByteArray? =
        saveManager.run { list().firstOrNull()?.let { load(PublicKey(it)) } }
}
