package com.project.skripsi.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.project.skripsi.data.repository.AccelerometerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AccelerometerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AccelerometerRepository(application)

    private val _x = MutableStateFlow(0f)
    private val _y = MutableStateFlow(0f)
    private val _z = MutableStateFlow(0f)
    val x: StateFlow<Float> = _x
    val y: StateFlow<Float> = _y
    val z: StateFlow<Float> = _z

    init {
        viewModelScope.launch {
            repository.sensorDataFlow.collect { (xVal, yVal, zVal) ->
                _x.value = xVal
                _y.value = yVal
                _z.value = zVal
            }
        }
    }

    fun startSensor() = repository.start()
    fun stopSensor() = repository.stop()
}
