package com.project.skripsi.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.project.skripsi.models.AccelerometerData

class AccelerometerSensor(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    // Realtime value (yang tampil di header)
    val x = mutableStateOf(0f)
    val y = mutableStateOf(0f)
    val z = mutableStateOf(0f)

    // List penyimpanan data terakhir (maks 60)
    val dataList = mutableStateListOf<AccelerometerData>()

    private var lastUpdateTime = 0L // untuk kontrol frekuensi

    fun startListening() {
        accelerometer?.also {
            // SENSOR_DELAY_FASTEST agar bisa dikontrol manual jadi 20Hz
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val currentTime = System.currentTimeMillis()

            // Batasi pembacaan setiap 50 ms (20 Hz)
            if (currentTime - lastUpdateTime >= 50) {
                lastUpdateTime = currentTime

                val xVal = it.values[0]
                val yVal = it.values[1]
                val zVal = it.values[2]

                x.value = xVal
                y.value = yVal
                z.value = zVal

                // Simpan data baru
                dataList.add(0, AccelerometerData(xVal, yVal, zVal, currentTime))

                // Hapus data lama jika lebih dari 60 (kompatibel)
                if (dataList.size > 60) {
                    dataList.removeAt(dataList.lastIndex)
                    // atau: dataList.removeAt(dataList.size - 1)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}