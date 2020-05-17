package de.patrickable.melderhelfer

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat

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
