package com.project.skripsi.ui.accelerometer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.skripsi.models.AccelerometerData
import com.project.skripsi.ui.theme.SkripsiTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccelerometerScreen(
    xValue: Float,
    yValue: Float,
    zValue: Float,
    dataList: List<AccelerometerData>,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Accelerometer Sensor") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // --- Bagian pembacaan realtime ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "X: %.2f".format(xValue), style = MaterialTheme.typography.titleMedium)
                Text(text = "Y: %.2f".format(yValue), style = MaterialTheme.typography.titleMedium)
                Text(text = "Z: %.2f".format(zValue), style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Gerakkan HP untuk melihat perubahan nilai sensor!",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Bagian daftar data terakhir ---
            Text(
                text = "Riwayat Pembacaan (maks 60 data):",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
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