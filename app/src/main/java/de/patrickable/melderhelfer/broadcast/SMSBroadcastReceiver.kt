package de.patrickable.melderhelfer.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import androidx.core.content.ContextCompat.startActivity

import de.patrickable.melderhelfer.AlarmActivity
import de.patrickable.melderhelfer.core.Settings

class SMSBroadcastReceiver : BroadcastReceiver()
{
    companion object {
        const val TAG = "SMSBroadcastReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

        if (context == null) {
            return
        }

        for (message in messages) {
            if (Settings.getPhoneNumber(context) != message.displayOriginatingAddress) {
                return
            }

            startActivity(
                context,
                Intent(context, AlarmActivity::class.java).apply {
                    putExtra(AlarmActivity.EXTRA_ALARM_CONTENT, message.displayMessageBody)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                },
                null
            )
        }
    }
}
