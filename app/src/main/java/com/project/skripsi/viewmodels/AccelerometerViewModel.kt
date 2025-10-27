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
            if (dataList.size > 60) dataList.removeAt(dataList.lastIndex)
        }
    }

    val xValue = mutableStateOf(0f)
    val yValue = mutableStateOf(0f)
    val zValue = mutableStateOf(0f)
    val dataList = mutableStateListOf<AccelerometerData>()
    val isRecording = mutableStateOf(false)

    fun startRecording() {
        if (isRecording.value) return

        isRecording.value = true
//        dataList.clear()
        accelerometerSensor.startListening()
        Log.d("AccelerometerVM", "Recording started")
    }

    fun stopRecording() {
        if (!isRecording.value) return

        isRecording.value = false
        accelerometerSensor.stopListening()
        Log.d("AccelerometerVM", "Recording stopped")
    }

    override fun onCleared() {
        super.onCleared()
        accelerometerSensor.stopListening()
    }
}
