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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor

class AccelerometerViewModel(context: Context) : ViewModel() {

    private val model: Module =
        PytorchModelLoader.load(context, "lstm_model_scripted.pt")

    private val sensor = AccelerometerSensor(context)

    private var sensorJob: Job? = null
    private val windowSize = 60
    private val window = ArrayDeque<Triple<Float, Float, Float>>(windowSize)

    private val _dataList = MutableStateFlow<List<AccelerometerData>>(emptyList())
    val dataList = _dataList.asStateFlow()

    private val _predictedClass = MutableStateFlow("...")
    val predictedClass = _predictedClass.asStateFlow()

    val xValue = MutableStateFlow(0f)
    val yValue = MutableStateFlow(0f)
    val zValue = MutableStateFlow(0f)

    val isRecording = MutableStateFlow(false)

    fun startRecording() {
        isRecording.value = true
        sensor.start()

        sensorJob = viewModelScope.launch(Dispatchers.Default) {
            try {
                for (sample in sensor.dataChannel) {
                    val (x, y, z) = sample

                    xValue.value = x
                    yValue.value = y
                    zValue.value = z

                    window.addLast(sample)
                    if (window.size > windowSize) window.removeFirst()

                    _dataList.value = window.map {
                        AccelerometerData(it.first, it.second, it.third, System.currentTimeMillis())
                    }

                    if (window.size == windowSize) {
                        runInference(window.toList())
                    }
                }
            } catch (e: ClosedReceiveChannelException) {
                Log.d("AccelerometerVM", "Channel closed, stopping loop.")
            }
        }
    }

    fun stopRecording() {
        isRecording.value = false
        sensor.stop()
        sensorJob?.cancel()
        sensorJob = null
    }

    private suspend fun runInference(data: List<Triple<Float, Float, Float>>) {
        withContext(Dispatchers.Default) {

            val flat = FloatArray(windowSize * 3)
            var idx = 0

            for ((x, y, z) in data) {
                flat[idx++] = (x - HarModelConfig.mean[0]) / HarModelConfig.scale[0]
                flat[idx++] = (y - HarModelConfig.mean[1]) / HarModelConfig.scale[1]
                flat[idx++] = (z - HarModelConfig.mean[2]) / HarModelConfig.scale[2]
            }

            val inputTensor = Tensor.fromBlob(
                flat,
                longArrayOf(1, windowSize.toLong(), 3)
            )

            val output = model.forward(IValue.from(inputTensor)).toTensor().dataAsFloatArray

            val bestIdx = output.indices.maxByOrNull { output[it] } ?: -1
            val label = if (bestIdx == -1) "Unknown"
            else HarModelConfig.labels[bestIdx]

            _predictedClass.value = label
        }
    }

    override fun onCleared() {
        sensorJob?.cancel()
        sensor.stop()
        super.onCleared()
    }
}
