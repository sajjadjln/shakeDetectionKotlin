package com.example.shakingdetector

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.shakingdetector.ui.theme.ShakingDetectorTheme
import java.util.Objects
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            Objects.requireNonNull(sensorManager)?.registerListener(sensorListener, sensorManager!!
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
            acceleration = 6f
            currentAcceleration = SensorManager.GRAVITY_EARTH //standard acceleration
            lastAcceleration = SensorManager.GRAVITY_EARTH //baseline for calculating changes
        }
    }
    private val sensorListener: SensorEventListener = object : SensorEventListener { //anonymous object
        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            //measures acceleration
            //Log.d("SensorManagers", "onSensorChanged: x: $x, y: $y, z: $z")
            lastAcceleration = currentAcceleration
            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.99f + delta //applies a smoothing factor (0.9f) to the existing acceleration and adds the change in acceleration (delta). This is a way to smooth out rapid changes in acceleration and focus on gradual changes
            Log.d("SensorManagers", "onSensorChanged: acceleration: $acceleration")
            if (acceleration > 8) {
                Toast.makeText(this@MainActivity, "Shake detected!", Toast.LENGTH_SHORT).show()
            }
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }
    override fun onResume() {
        sensorManager?.registerListener(sensorListener, sensorManager!!.getDefaultSensor(
            Sensor .TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
        )
        super.onResume()
    }
    override fun onPause() {
        sensorManager!!.unregisterListener(sensorListener)
        super.onPause()
    }
}

