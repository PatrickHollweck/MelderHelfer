package de.patrickable.melderhelfer.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.provider.Telephony
import androidx.core.content.ContextCompat.startActivity

import de.patrickable.melderhelfer.core.parsers.AlarmSMSParser


class SMSBroadcastReceiver : BroadcastReceiver()
{
    companion object {
        const val TAG = "SMSBroadcastReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

        // TODO: Check the phone number if it matches the "watches" numbers

        for (message in messages) {
            val alarm = AlarmSMSParser.parseMessage(message.displayMessageBody)

            Log.i(TAG, "Alarm Received:\n$alarm")

            if (alarm.address != null) {
                startGoogleMaps(alarm.address!!, context!!)
            }

        }
    }

    private fun startGoogleMaps(address: String, context: Context) {
        val navigationTarget = Uri.encode(address)
        val intentTarget = Uri.parse("google.navigation:q=${navigationTarget}")

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
