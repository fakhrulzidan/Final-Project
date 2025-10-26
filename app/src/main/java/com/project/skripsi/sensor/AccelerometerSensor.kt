package com.project.skripsi.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.*
import android.util.Log
import java.util.concurrent.atomic.AtomicBoolean


class AccelerometerSensor(
    context: Context,
    private val onDataCollected: (x: Float, y: Float, z: Float) -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var latestX = 0f
    private var latestY = 0f
    private var latestZ = 0f

    private var samplingJob: Job? = null
    private val isRecording = AtomicBoolean(false)

    fun startListening() {
        if (accelerometer == null) {
            Log.e("AccelerometerSensor", "No accelerometer sensor found!")
            return
        }

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST)

        if (isRecording.compareAndSet(false, true)) {
            samplingJob = CoroutineScope(Dispatchers.Default).launch {
                val targetInterval = 50L // 20 Hz → setiap 50 ms
                var sampleCount = 0
                val startTime = System.currentTimeMillis()

                // Tambahan untuk debugging per detik
                var countInThisSecond = 0
                var lastSecondMark = startTime

                while (isActive && isRecording.get()) {
                    val t1 = System.currentTimeMillis()

                    // ambil snapshot dari data terbaru
                    onDataCollected(latestX, latestY, latestZ)
                    sampleCount++
                    countInThisSecond++

                    delay(targetInterval)

                    val now = System.currentTimeMillis()
                    val elapsed = now - startTime

                    // Debugging: log setiap 1 detik
                    if (now - lastSecondMark >= 1000L) {
                        Log.d("AccelerometerSensor", "1s window: $countInThisSecond samples")
                        countInThisSecond = 0
                        lastSecondMark = now
                    }

                    if (sampleCount % 20 == 0) { // log total setiap ~1 detik juga
                        val elapsedSec = elapsed / 1000.0
                        Log.d("AccelerometerSensor", "SampleCount=$sampleCount after ${"%.2f".format(elapsedSec)}s")
                    }

                    val diff = System.currentTimeMillis() - t1
                    if (diff > targetInterval + 5) {
                        Log.w("AccelerometerSensor", "Loop delay too high: ${diff}ms")
                    }
                }
            }
            Log.d("AccelerometerSensor", "Started software sampling at 20Hz.")
        }
    }


    fun stopListening() {
        if (isRecording.compareAndSet(true, false)) {
            samplingJob?.cancel()
            sensorManager.unregisterListener(this)
            Log.d("AccelerometerSensor", "Stopped listening.")
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        // Simpan data terakhir yang dibaca
        latestX = event.values[0]
        latestY = event.values[1]
        latestZ = event.values[2]
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}