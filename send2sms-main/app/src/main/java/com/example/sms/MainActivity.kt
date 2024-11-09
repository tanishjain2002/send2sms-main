package com.example.sms

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.telephony.SmsManager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var num: EditText
    private lateinit var msg: EditText
    private lateinit var btn: Button
    private val SEND_SMS_REQUEST_CODE = 123 // Define a request code

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        num = findViewById(R.id.num)
        msg = findViewById(R.id.msg)
        btn = findViewById(R.id.btn)

        btn.setOnClickListener {
            // Request the SEND_SMS permission if not granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), SEND_SMS_REQUEST_CODE)
            } else {
                // Permission already granted, proceed with sending SMS
                sendSMS()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SEND_SMS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with sending SMS
                sendSMS()
            } else {
                // Permission denied, show AlertDialog
                showPermissionAlertDialog()
            }
        }
    }

    private fun sendSMS() {
        val phnNum = num.text.toString()
        val message = msg.text.toString()
        Log.d("MainActivity", "Phone Number: $phnNum, Message: $message")// Rename variable to avoid confusion with outer 'msg' variable
        val smsManager: SmsManager = SmsManager.getDefault()
        try {
            Toast.makeText(this, "Sending SMS...", Toast.LENGTH_SHORT).show()
            smsManager.sendTextMessage(phnNum, null, message, null, null)
            Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error sending SMS: ${e.message}") // Log any exceptions that occur during SMS sending
            Toast.makeText(this, "Failed to send SMS", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPermissionAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permission Required")
            .setMessage("This app requires the SEND_SMS permission to send SMS messages.")
            .setPositiveButton("Go to settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
