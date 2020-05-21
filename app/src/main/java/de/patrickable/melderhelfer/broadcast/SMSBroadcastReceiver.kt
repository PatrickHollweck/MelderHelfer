package de.patrickable.melderhelfer.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import androidx.core.content.ContextCompat.startActivity

import de.patrickable.melderhelfer.AlarmActivity

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

        // TODO: Check the phone number if it matches the "watches" numbers

        for (message in messages) {
            startActivity(
                context,
                Intent(context, AlarmActivity::class.java).apply {
                    putExtra(AlarmActivity.EXTRA_ALARM_CONTENT, message.displayMessageBody)
                    setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                },
                null
            )
        }
    }
}
