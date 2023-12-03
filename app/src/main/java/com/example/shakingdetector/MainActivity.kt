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

class MainActivity : ComponentActivity(), ShakeDetector.OnShakeListener {
    private lateinit var shakeDetector: ShakeDetector
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shakeDetector = ShakeDetector(this, this)
    }
    override fun onShake() {
        Toast.makeText(this, "Shake detected!", Toast.LENGTH_SHORT).show()
    }
    override fun onResume() {
        super.onResume()
        shakeDetector.onResume()
    }
    override fun onPause() {
        super.onPause()
        shakeDetector.onPause()
    }
}

