package com.project.skripsi.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.*
import android.util.Log
import kotlinx.coroutines.channels.Channel
import java.util.concurrent.atomic.AtomicBoolean


class AccelerometerSensor(
    context: Context,
    private val onDataCollected: ((Float, Float, Float) -> Unit)? = null
) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accelerometer =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    val dataChannel = Channel<Triple<Float, Float, Float>>(capacity = Channel.UNLIMITED)

    // Debug variables
    private var sampleCount = 0
    private var countInThisSecond = 0
    private var lastSecondMark = System.currentTimeMillis()
    private var startTime = 0L

    fun start() {
        if (accelerometer == null) {
            Log.e("AccelerometerSensor", "No accelerometer found")
            return
        }

        sampleCount = 0
        countInThisSecond = 0
        lastSecondMark = System.currentTimeMillis()
        startTime = lastSecondMark

        sensorManager.registerListener(
            this,
            accelerometer,
            50000   // target 20 Hz
        )

        Log.d("AccelerometerSensor", "Sensor started with target 20 Hz")
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        dataChannel.close()
        Log.d("AccelerometerSensor", "Sensor stopped + channel closed")
    }

    override fun onSensorChanged(event: SensorEvent) {
        val t1 = System.currentTimeMillis()

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        dataChannel.trySend(Triple(x, y, z))

        // Debug counters
        sampleCount++
        countInThisSecond++

        val now = System.currentTimeMillis()
        val elapsed = now - startTime

        // Log setiap 1 detik
        if (now - lastSecondMark >= 1000L) {
            Log.d("AccelerometerSensor", "1s window: $countInThisSecond samples")
            countInThisSecond = 0
            lastSecondMark = now
        }

        // Log total tiap ~20 sampel (~1 detik)
        if (sampleCount % 20 == 0) {
            val elapsedSec = elapsed / 1000.0
            Log.d(
                "AccelerometerSensor",
                "SampleCount=$sampleCount after ${"%.2f".format(elapsedSec)}s"
            )
        }

        // Delay check
        val diff = System.currentTimeMillis() - t1
        val targetInterval = 50    // ms per sample @ 20 Hz

        if (diff > targetInterval + 5) {
            Log.w("AccelerometerSensor", "Loop delay too high: ${diff}ms")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
