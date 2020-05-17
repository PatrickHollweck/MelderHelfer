package de.patrickable.melderhelfer.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Telephony
import android.util.Log
import androidx.core.content.ContextCompat.startActivity


class SMSBroadcastReceiver : BroadcastReceiver()
{
    companion object {
        const val TAG = "SMSBroadcastReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

        for (message in messages) {
            Log.i(TAG, "SMS Received: '${message.displayMessageBody}'")
        }
    }

    fun startGoogleMaps(address: String, context: Context) {
        val intentTarget = Uri.parse("google.navigation:q=${Uri.encode(address)}")

        val mapIntent = Intent(
            Intent.ACTION_VIEW,
            intentTarget
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            setPackage("com.google.android.apps.maps")
        }

        startActivity(context, mapIntent, null)
    }
}
