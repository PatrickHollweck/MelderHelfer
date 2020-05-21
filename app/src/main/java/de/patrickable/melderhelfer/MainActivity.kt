package de.patrickable.melderhelfer

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import de.patrickable.melderhelfer.core.Settings
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        val RUNTIME_PERMISSIONS = arrayOf(
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.BROADCAST_SMS
        )

        const val TAG = "MainActivity"
        const val SMS_PERMISSION_REQUEST_CODE = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestSMSPermissions()

        buttonTestAlarm.setOnClickListener() {
            ContextCompat.startActivity(
                this,
                Intent(this, AlarmActivity::class.java).apply {
                    putExtra(
                        AlarmActivity.EXTRA_ALARM_CONTENT,
                        """
                        19:24 
                        EINSATZORT: Gasfabrikstraße Haus-Nr.: 19 , Amberg

                        EINSATZGRUND: #R2010#Atmung#vitale Bedrohung
                        RD 2
                        """.trimIndent()
                    )
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                },
                null
            )
        }

        buttonSave.setOnClickListener() {
            Settings.setIsMute(this, checkBoxMute.isChecked)
            Settings.setPhoneNumber(this, editTargetPhoneInput.text.toString())
            Settings.setWhatsappGroupLink(this, editWhatsappGroupInput.text.toString())

            Toast.makeText(this, "Einstellungen gespeichert!", Toast.LENGTH_SHORT).show()
        }

        checkBoxMute.isChecked = Settings.getIsMute(this)
        editTargetPhoneInput.setText(Settings.getPhoneNumber(this))
        editWhatsappGroupInput.setText(Settings.getWhatsappGroupLink(this))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            SMS_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    Log.i(TAG, "Permissions granted...")
                } else {
                    Log.i(TAG, "Permissions denied...")
                }
            }
        }
    }

    private fun requestSMSPermissions() {
        ActivityCompat.requestPermissions(
            this,
            RUNTIME_PERMISSIONS,
            SMS_PERMISSION_REQUEST_CODE
        )
    }
}
