package com.developersmarket.stairshelper

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log

class AccelerometerSensor(private var context: Context) : SensorEventListener {
    private var sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometerSensor: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var vibration: Vibration = Vibration(context)
    private var isDown = false
    private val TAG = "AccelerometerSensor"
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("vibration_settings", Context.MODE_PRIVATE)
    private var vibrationEffect: VibrationEffect? = null

    fun start() {
        loadVibrationSettings()
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            // Get the x, y, and z values of the accelerometer
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val angle = Math.atan2(y.toDouble(), x.toDouble()) * 180 / Math.PI
            if(angle < -45 && angle > -135 && !isDown){
                isDown = true
                //to use below code later
//                (this.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(vibrationEffect)

                vibration.vibrate()

                Log.d(TAG, "onSensorChanged() called with: event = $event")
            }
            if(angle < 45 && angle > -45 && isDown){
                isDown = false
                vibration.stop()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    private fun loadVibrationSettings() {
        val duration = sharedPreferences.getLong("duration", 200)
        val strength = sharedPreferences.getInt("strength", 50)
        val frequency = sharedPreferences.getInt("frequency", 0)
        val pattern = sharedPreferences.getInt("pattern", 0)

        when (pattern) {
            0 -> vibrationEffect = VibrationEffect.createOneShot(duration, strength)
            1 -> vibrationEffect = VibrationEffect.createWaveform(
                longArrayOf(0, duration),
                intArrayOf(0, strength),
                frequency
            )
            2 -> vibrationEffect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
        }
    }
}
