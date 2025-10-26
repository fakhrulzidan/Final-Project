package com.project.skripsi.ui.accelerometer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.project.skripsi.data.models.AccelerometerData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccelerometerScreen(
    xValue: Float,
    yValue: Float,
    zValue: Float,
    dataList: List<AccelerometerData>,
    isRecording: Boolean,
    countdown: Int,
    onStart: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Accelerometer Sensor") }) }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("X: %.2f".format(xValue))
            Text("Y: %.2f".format(yValue))
            Text("Z: %.2f".format(zValue))

            Spacer(modifier = Modifier.height(12.dp))

            if (countdown > 0) {
                Text(
                    text = "Recording... ($countdown s)",
                    color = Color.Red,
                    style = MaterialTheme.typography.titleMedium
                )
            } else if (isRecording) {
                Text(
                    text = "Recording...",
                    color = Color.Red,
                    style = MaterialTheme.typography.titleMedium
                )
            } else {
                Text(
                    text = "Idle",
                    color = Color.Gray,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = onStart, enabled = !isRecording) {
                    Text("Start 3s Recording")
                }
                Button(onClick = onStop, enabled = isRecording) {
                    Text("Stop")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Riwayat Pembacaan (maks 200 data):", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(dataList) { index, data ->
                    Text(
                        text = "${index + 1}. X=${"%.2f".format(data.x)}, Y=${"%.2f".format(data.y)}, Z=${"%.2f".format(data.z)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
