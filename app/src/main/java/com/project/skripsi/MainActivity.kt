package com.project.skripsi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.activity.viewModels
import com.project.skripsi.ui.components.CustomBottomNavBar
import com.project.skripsi.ui.accelerometer.AccelerometerScreen
import com.project.skripsi.viewmodels.AccelerometerViewModel
import com.project.skripsi.viewmodels.AccelerometerViewModelFactory


class MainActivity : ComponentActivity() {
    private val viewModel: AccelerometerViewModel by viewModels {
        AccelerometerViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Scaffold(
                    bottomBar = { CustomBottomNavBar() }
                ) { innerPadding ->
                    AccelerometerScreen(
                        xValue = viewModel.xValue.value,
                        yValue = viewModel.yValue.value,
                        zValue = viewModel.zValue.value,
                        dataList = viewModel.dataList,
                        isRecording = viewModel.isRecording.value,
                        countdown = viewModel.countdown.value,
                        onStart = { viewModel.startRecording() },
                        onStop = { viewModel.stopRecording() },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

