package com.project.skripsi.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.*
import android.util.Log
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean


class AccelerometerSensor(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    // hanya menyimpan 1 nilai terbaru → TIDAK ada overflow
    private val _sensorFlow = MutableStateFlow(Triple(0f, 0f, 0f))
    val sensorFlow: StateFlow<Triple<Float, Float, Float>> = _sensorFlow.asStateFlow()

    fun start() {
        if (accelerometer == null) {
            Log.e("AccSensorFlow", "Accelerometer not available")
            return
        }
        sensorManager.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_FASTEST
        )
        Log.d("AccSensorFlow", "Started reading sensor (FASTEST)")
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        Log.d("AccSensorFlow", "Stopped sensor")
    }

    override fun onSensorChanged(event: SensorEvent) {
        _sensorFlow.value = Triple(
            event.values[0],
            event.values[1],
            event.values[2]
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
