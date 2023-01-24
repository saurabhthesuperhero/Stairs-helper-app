package com.developersmarket.stairshelper

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class AccelerometerSensor(context: Context) : SensorEventListener {
    private var sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometerSensor: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var vibration: Vibration = Vibration(context)
    private var isDown = false

    fun start() {
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
                vibration.vibrate()
            }
            if(angle < 45 && angle > -45 && isDown){
                isDown = false
                vibration.stop()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
}
