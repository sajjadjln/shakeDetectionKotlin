package com.example.shakingdetector

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import java.lang.StrictMath.sqrt
import java.util.Objects

class SensorManagers(context: Context, onShaked: () -> Unit) {
    private var sensorManager: SensorManager? = null
    private var acceleration = 10f
    private val shakeThreshold = 6
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f

    private val SHAKE_THRESHOLD_GRAVITY = 3.7f
    private val SHAKE_SLOP_TIME_MS = 300
    private val SHAKE_COUNT_RESET_TIME_MS = 3000

    private var mShakeTimestamp: Long = 0
    private var mShakeCount = 0
    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val gX: Float = x / SensorManager.GRAVITY_EARTH
            val gY: Float = y / SensorManager.GRAVITY_EARTH
            val gZ: Float = z / SensorManager.GRAVITY_EARTH

            val gForce = sqrt((gX * gX + gY * gY + gZ * gZ).toDouble()).toFloat()


            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                val now = System.currentTimeMillis()
                // ignore shake events too close to each other (500ms)
                if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    return
                }
                // reset the shake count after 3 seconds of no shakes
                if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                    mShakeCount = 0
                }
                mShakeTimestamp = now
                mShakeCount++
                onShaked()
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            Log.d("SensorManagers", "Accuracy changed: $accuracy")
        }
    }
    init {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        Objects.requireNonNull(sensorManager)?.registerListener(
            sensorListener,
            sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH
    }

    fun registerListener() {
        sensorManager?.registerListener(
            sensorListener,
            sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun unRegisterListener() {
        sensorManager?.unregisterListener(sensorListener)
    }
}