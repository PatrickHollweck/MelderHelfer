package de.patrickable.melderhelfer

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import de.patrickable.melderhelfer.core.models.AlarmSMS
import de.patrickable.melderhelfer.core.parsers.AlarmSMSParser
import de.patrickable.melderhelfer.core.util.EasyTextToSpeach
import kotlinx.android.synthetic.main.activity_alarm.*


class AlarmActivity : AppCompatActivity() {
    companion object {
        const val TAG = "AlarmActivity"
        const val EXTRA_ALARM_CONTENT = "alarm_content"
    }

    private var rawAlarmContent: String? = null
    private var parsedAlarm: AlarmSMS? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        val rawAlarmContent = intent?.extras?.getString(EXTRA_ALARM_CONTENT)

        if (rawAlarmContent != null) {
            updateAlarm(rawAlarmContent)
        }

        buttonStartWhatsapp.setOnClickListener() {
            startWhatsapp()
        }

        buttonStartMaps.setOnClickListener() {
            if (parsedAlarm?.address != null) {
                startGoogleMaps(parsedAlarm!!.address as String)
            }
        }
    }

    private fun updateAlarm(alarmContent: String) {
        rawAlarmContent = alarmContent

        textAlarmContent.text = alarmContent

        try {
            val parsed = AlarmSMSParser.parseMessage(alarmContent)
            this.parsedAlarm = parsed

            textToSpeechAlarm()
        } catch (e: Exception) {
            Log.e(TAG, "Alarm parse exception", e)
        }
    }

    private fun startWhatsapp() {
        val packageName = "com.whatsapp"

        if (!isPackageInstalled(packageName, this)) {
            Toast
                .makeText(this, "Whatsapp is not installed!", Toast.LENGTH_SHORT)
                .show()

            return
        }

        val groupID = "xxx";

        val startMapsIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://chat.whatsapp.com/$groupID")
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            setPackage(packageName)
        }

        ContextCompat.startActivity(this, startMapsIntent, null)
    }

    private fun startGoogleMaps(address: String) {
        val navigationTarget = Uri.encode(address)
        val packageName = "com.google.android.apps.maps"
        val intentTarget = Uri.parse("google.navigation:q=${navigationTarget}")

        if (!isPackageInstalled(packageName, this)) {
            Toast
                .makeText(this, "Google maps is not installed!", Toast.LENGTH_SHORT)
                .show()

            return
        }

        val startMapsIntent = Intent(
            Intent.ACTION_VIEW,
            intentTarget
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            setPackage("com.google.android.apps.maps")
        }

        ContextCompat.startActivity(this, startMapsIntent, null)
    }

    private fun isPackageInstalled(packageName: String, context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun textToSpeechAlarm() {
        parsedAlarm.let {
            val ttsText = "SMS ALARM ..H..V..O... ${parsedAlarm?.dispatchType} .... ${parsedAlarm?.shortCodeword()} ... ${parsedAlarm?.address}"

            speak(ttsText)
            Handler().postDelayed({
                speak(ttsText)
            }, 15000)
        }
    }

    private fun speak(message: String) {
        EasyTextToSpeach.say(this, message)
    }
}
