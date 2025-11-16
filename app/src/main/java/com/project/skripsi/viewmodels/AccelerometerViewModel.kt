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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor

class AccelerometerViewModel(context: Context) : ViewModel() {

    // --- Load TorchScript model ---
    private val model: Module = PytorchModelLoader.load(context, "lstm_model_scripted.pt")

    // --- Sensor implementation you already have ---
    private val accelerometerSensor = AccelerometerSensor(context) { x, y, z ->
        xValue.value = x
        yValue.value = y
        zValue.value = z

        if (isRecording.value) {
            dataList.add(0, AccelerometerData(x, y, z, System.currentTimeMillis()))

            if (dataList.size > windowSize) {
                dataList.removeAt(dataList.lastIndex)
            }

            // RUN INFERENCE WHEN WINDOW FULL
            if (dataList.size == windowSize) {
                runInference()
            }
        }
    }

    val xValue = mutableStateOf(0f)
    val yValue = mutableStateOf(0f)
    val zValue = mutableStateOf(0f)

    val dataList = mutableStateListOf<AccelerometerData>()
    val predictedClass = mutableStateOf("...")   // <--- For UI

    val isRecording = mutableStateOf(false)

    private val windowSize = 60   // adjust to your training setup

    fun startRecording() {
        if (isRecording.value) return
//        dataList.clear()
//        predictedClass.value = -1
        isRecording.value = true
        accelerometerSensor.startListening()
    }

    fun stopRecording() {
        if (!isRecording.value) return
        isRecording.value = false
        accelerometerSensor.stopListening()
    }

    // --- PREPARE INPUT AND RUN MODEL ---
    private fun runInference() {
        // Prepare buffer of shape (60, 3)
        val flat = FloatArray(windowSize * 3)

        var idx = 0
        dataList.forEach { sample ->
            // --- Normalize each axis using training StandardScaler ---
            flat[idx++] = (sample.x - HarModelConfig.mean[0]) / HarModelConfig.scale[0]
            flat[idx++] = (sample.y - HarModelConfig.mean[1]) / HarModelConfig.scale[1]
            flat[idx++] = (sample.z - HarModelConfig.mean[2]) / HarModelConfig.scale[2]
        }

        // PyTorch input shape: (1, 60, 3)
        val inputTensor = Tensor.fromBlob(
            flat,
            longArrayOf(1, windowSize.toLong(), 3)
        )

        val output = model.forward(IValue.from(inputTensor)).toTensor()
        val logits = output.dataAsFloatArray

        // Argmax
        val predictedIndex = logits.indices.maxByOrNull { logits[it] } ?: -1

        // Convert integer class → label name
        predictedClass.value =
            if (predictedIndex == -1) "Unknown"
            else HarModelConfig.labels[predictedIndex]
    }
    override fun onCleared() {
        accelerometerSensor.stopListening()
        super.onCleared()
    }
}
