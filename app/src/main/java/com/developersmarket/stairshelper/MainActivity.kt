package com.developersmarket.stairshelper

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.developersmarket.stairshelper.databinding.ActivityMainBinding
import com.developersmarket.stairshelper.databinding.ActivitySettingsBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var accelerometerSensor: AccelerometerSensor
    private lateinit var vibration: Vibration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        accelerometerSensor = AccelerometerSensor(this)
        vibration = Vibration(this)

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java).apply {
                // you can add values(if any) to pass to the next class or avoid using `.apply`
                putExtra("keyIdentifier", "Dummy")
            })
        }
    }


    override fun onResume() {
        super.onResume()

        // Check if the user granted permission to access the accelerometer sensor
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED) {
            accelerometerSensor.start()
        } else {
            // Request permission from the user
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.BODY_SENSORS), 1)
        }
    }


    override fun onPause() {
        super.onPause()
        accelerometerSensor.stop()
        vibration.stop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Start listening to the accelerometer sensor
            accelerometerSensor.start()
        } else {
            // Permission denied, notify the user
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }
}

