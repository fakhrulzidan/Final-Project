package com.project.skripsi.data.repository

import android.content.Context
import com.project.skripsi.sensor.AccelerometerSensor
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AccelerometerRepository(context: Context) {

    private val _sensorDataFlow = MutableSharedFlow<Triple<Float, Float, Float>>(replay = 1)
    val sensorDataFlow = _sensorDataFlow.asSharedFlow()

    private val accelerometerSensor = AccelerometerSensor(context) { x, y, z ->
        _sensorDataFlow.tryEmit(Triple(x, y, z))
    }

    fun start() {
        accelerometerSensor.startListening()
    }

    fun stop() {
        accelerometerSensor.stopListening()
    }
}
