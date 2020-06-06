package de.patrickable.melderhelfer

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import de.patrickable.melderhelfer.core.Settings
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
            this.parsedAlarm = AlarmSMSParser.parseMessage(alarmContent)

            playAlarmSounds()
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

        val whatsappGroupLink = Settings.getWhatsappGroupLink(this)

        if (whatsappGroupLink.isEmpty()) {
            ContextCompat.startActivity(
                this,
                packageManager.getLaunchIntentForPackage(packageName)!!,
                null
            )
        } else {
            ContextCompat.startActivity(
                this,
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(Settings.getWhatsappGroupLink(this))
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    setPackage(packageName)
                },
                null
            )
        }
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

    private fun playAlarmSounds() {
        if (Settings.getIsMute(this)) {
            return
        }

        try {
            EasyTextToSpeach.unMuteSounds(this)
            val mediaPlayer = MediaPlayer.create(this, R.raw.pieper)

            if (!Settings.getIsTTSMuted(this)) {
                mediaPlayer.setOnCompletionListener {
                    parsedAlarm.let {
                        val ttsText = "SMS ALARM ..H..V..O... ${parsedAlarm?.dispatchType} .... ${parsedAlarm?.shortCodeword()} ... ${parsedAlarm?.address}"

                        EasyTextToSpeach.say(this, ttsText)
                        Handler().postDelayed({
                            EasyTextToSpeach.say(this, ttsText)
                        }, 15000)
                    }
                }
            }

            mediaPlayer.start()

        } catch (e: Exception) {
            Log.e(TAG, "Failed to play alarm sound", e)
        }
    }
}
