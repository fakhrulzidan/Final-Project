package com.project.skripsi.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.skripsi.data.ml.HarModelConfig
import com.project.skripsi.data.models.AccelerometerData
import com.project.skripsi.sensor.AccelerometerSensor
import com.project.skripsi.utils.PytorchModelLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor

class AccelerometerViewModel(context: Context) : ViewModel() {

    private val model: Module = PytorchModelLoader.load(context, "lstm_model_scripted.pt")

    private val sensor = AccelerometerSensor(context)

    val xValue = mutableStateOf(0f)
    val yValue = mutableStateOf(0f)
    val zValue = mutableStateOf(0f)

    val predictedClass = mutableStateOf("...")
    val isRecording = mutableStateOf(false)
    val dataList = mutableStateListOf<AccelerometerData>()

    private val windowSize = 60

    init {
        collectSensorData()
    }

    private fun collectSensorData() {
        viewModelScope.launch {

            sensor.sensorFlow
                .sample(50)
                .collect { (x, y, z) ->

                    // Update UI
                    xValue.value = x
                    yValue.value = y
                    zValue.value = z

                    if (isRecording.value) {
                        dataList.add(0, AccelerometerData(x, y, z, System.currentTimeMillis()))

                        if (dataList.size > windowSize)
                            dataList.removeAt(dataList.lastIndex)

                        if (dataList.size == windowSize)
                            runInference()
                    }
                }
        }
    }

    fun startRecording() {
        if (isRecording.value) return
//        dataList.clear()
        predictedClass.value = "..."
        isRecording.value = true
        sensor.start()
    }

    fun stopRecording() {
        if (!isRecording.value) return
        isRecording.value = false
        sensor.stop()
    }

    private fun runInference() {
        val windowCopy = dataList.toList()

        viewModelScope.launch(Dispatchers.Default) {

            val flat = FloatArray(windowSize * 3)
            var idx = 0

            windowCopy.forEach { sample ->
                flat[idx++] = (sample.x - HarModelConfig.mean[0]) / HarModelConfig.scale[0]
                flat[idx++] = (sample.y - HarModelConfig.mean[1]) / HarModelConfig.scale[1]
                flat[idx++] = (sample.z - HarModelConfig.mean[2]) / HarModelConfig.scale[2]
            }

            val tensor = Tensor.fromBlob(flat, longArrayOf(1, windowSize.toLong(), 3))
            val output = model.forward(IValue.from(tensor)).toTensor()
            val logits = output.dataAsFloatArray

            val index = logits.indices.maxByOrNull { logits[it] } ?: -1
            val label = if (index == -1) "Unknown" else HarModelConfig.labels[index]

            withContext(Dispatchers.Main) {
                predictedClass.value = label
            }
        }
    }

    override fun onCleared() {
        sensor.stop()
        super.onCleared()
    }
}
