package com.project.skripsi.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.skripsi.data.models.AccelerometerData
import com.project.skripsi.sensor.AccelerometerSensor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AccelerometerViewModel(context: Context) : ViewModel() {

    private val accelerometerSensor = AccelerometerSensor(context) { x, y, z ->
        xValue.value = x
        yValue.value = y
        zValue.value = z

        if (isRecording.value) {
            dataList.add(0, AccelerometerData(x, y, z, System.currentTimeMillis()))
            if (dataList.size > 200) dataList.removeAt(dataList.lastIndex)
        }
    }

    val xValue = mutableStateOf(0f)
    val yValue = mutableStateOf(0f)
    val zValue = mutableStateOf(0f)
    val dataList = mutableStateListOf<AccelerometerData>()
    val isRecording = mutableStateOf(false)
    val countdown = mutableStateOf(0)

    fun startRecording() {
        if (isRecording.value) return

        isRecording.value = true
        dataList.clear()
        accelerometerSensor.startListening()
        Log.d("AccelerometerVM", "Recording started")

        countdown.value = 3
        viewModelScope.launch {
            for (i in 3 downTo 1) {
                countdown.value = i
                delay(1000)
            }
            stopRecording()
        }
    }

    fun stopRecording() {
        if (!isRecording.value) return

        isRecording.value = false
        countdown.value = 0
        accelerometerSensor.stopListening()
        Log.d("AccelerometerVM", "Recording stopped")
    }

    override fun onCleared() {
        super.onCleared()
        accelerometerSensor.stopListening()
    }
}
