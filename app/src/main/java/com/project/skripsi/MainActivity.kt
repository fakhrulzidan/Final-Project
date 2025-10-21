package com.project.skripsi

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.skripsi.components.CustomBottomNavBar
import com.project.skripsi.sensors.AccelerometerSensor
import com.project.skripsi.ui.accelerometer.AccelerometerScreen

class MainActivity : ComponentActivity() {

    private lateinit var accelerometerSensor: AccelerometerSensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accelerometerSensor = AccelerometerSensor(this)

        setContent {
            MaterialTheme {
                Scaffold(
                    bottomBar = { CustomBottomNavBar() }
                ) { innerPadding ->
                    AccelerometerScreen(
                        xValue = accelerometerSensor.x.value,
                        yValue = accelerometerSensor.y.value,
                        zValue = accelerometerSensor.z.value,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding) //
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometerSensor.startListening()
    }
    override fun onPause() {
        super.onPause()
        accelerometerSensor.stopListening()
    }
}