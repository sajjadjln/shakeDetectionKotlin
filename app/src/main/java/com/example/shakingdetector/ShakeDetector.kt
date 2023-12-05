package com.example.shakingdetector

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import java.util.Objects
import kotlin.math.sqrt

class ShakeDetector(private val context: Context, private val onShakeListener: OnShakeListener) :
    SensorEventListener {

    interface OnShakeListener {
        fun onShake()
    }

    private var sensorManager: SensorManager? = null
    private var acceleration = -10f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f
    private var lastShakeTime: Long = 0


    init {
        initializeSensorManager()
    }

    private fun initializeSensorManager() {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        Objects.requireNonNull(sensorManager)?.registerListener(
            this,
            sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        lastAcceleration = currentAcceleration
        currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

        val delta: Float = currentAcceleration - lastAcceleration
        acceleration = acceleration * 0.9f + delta

        val currentTime = System.currentTimeMillis()
        val timeElapsedSinceLastShake = currentTime - lastShakeTime
        Log.d("ShakeDetector", "acceleration: $acceleration")
        if (acceleration > 5.5 && timeElapsedSinceLastShake > 2000) {
            onShakeListener.onShake()
            lastShakeTime = currentTime
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Ignore
    }

    fun onResume() {
        sensorManager?.registerListener(
            this,
            sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun onPause() {
        sensorManager?.unregisterListener(this)
    }
    fun onDisable() {
        sensorManager?.unregisterListener(this)
    }
    fun onEnable() {
        sensorManager?.registerListener(
            this,
            sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }
}
