package com.project.skripsi

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.project.skripsi.ui.components.CustomBottomNavBar
import com.project.skripsi.data.models.AccelerometerData
import com.project.skripsi.sensor.AccelerometerSensor
import com.project.skripsi.ui.accelerometer.AccelerometerScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var accelerometerSensor: AccelerometerSensor
    private val dataList = mutableStateListOf<AccelerometerData>()
    private val xValue = mutableStateOf(0f)
    private val yValue = mutableStateOf(0f)
    private val zValue = mutableStateOf(0f)

    // Tambahan state
    private val isRecording = mutableStateOf(false)
    private val countdown = mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        accelerometerSensor = AccelerometerSensor(this) { x, y, z ->
            xValue.value = x
            yValue.value = y
            zValue.value = z

            if (isRecording.value) {
                dataList.add(0, AccelerometerData(x, y, z, System.currentTimeMillis()))
                if (dataList.size > 200) dataList.removeAt(dataList.lastIndex)
            }
        }

        setContent {
            MaterialTheme {
                Scaffold(
                    bottomBar = { CustomBottomNavBar() }
                ) { innerPadding ->
                    AccelerometerScreen(
                        xValue = xValue.value,
                        yValue = yValue.value,
                        zValue = zValue.value,
                        dataList = dataList,
                        isRecording = isRecording.value,
                        countdown = countdown.value,
                        onStart = { startRecording() },
                        onStop = { stopRecording() },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun startRecording() {
        if (isRecording.value) return

        isRecording.value = true
        dataList.clear()
        accelerometerSensor.startListening()
        Log.d("MainActivity", "Recording started")

        // Countdown UI: update tiap detik
        countdown.value = 3
        CoroutineScope(Dispatchers.Main).launch {
            for (i in 3 downTo 1) {
                countdown.value = i
                delay(1000)
            }
            stopRecording()
        }
    }

    private fun stopRecording() {
        if (!isRecording.value) return

        isRecording.value = false
        countdown.value = 0
        accelerometerSensor.stopListening()
        Log.d("MainActivity", "Recording stopped")
    }

    override fun onPause() {
        super.onPause()
        stopRecording()
    }
}

