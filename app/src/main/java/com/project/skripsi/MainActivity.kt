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
import androidx.compose.runtime.collectAsState
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
                        // Optional: if you want live x,y,z values
                        xValue = viewModel.xValue.collectAsState().value,
                        yValue = viewModel.yValue.collectAsState().value,
                        zValue = viewModel.zValue.collectAsState().value,

                        dataList = viewModel.dataList.collectAsState().value,     // if still needed
                        isRecording = viewModel.isRecording.collectAsState().value,
                        predictedActivity = viewModel.predictedClass.collectAsState().value,

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

