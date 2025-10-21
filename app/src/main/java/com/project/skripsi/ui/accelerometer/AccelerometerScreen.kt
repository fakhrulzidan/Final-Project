package com.project.skripsi.ui.accelerometer

import androidx.compose.foundation.layout.*
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
import com.project.skripsi.ui.theme.SkripsiTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccelerometerScreen(
    xValue: Float,
    yValue: Float,
    zValue: Float,
    modifier: Modifier= Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Accelerometer Sensor") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "X: %.2f".format(xValue), style = MaterialTheme.typography.titleMedium)
            Text(text = "Y: %.2f".format(yValue), style = MaterialTheme.typography.titleMedium)
            Text(text = "Z: %.2f".format(zValue), style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Gerakkan HP untuk melihat perubahan nilai sensor!",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAccelerometerScreen() {
    SkripsiTheme {   // Ganti dengan nama tema kamu (misal SkripsiTheme)
        AccelerometerScreen(
            xValue = 0.12f,
            yValue = -9.81f,
            zValue = 3.45f
        )
    }
}
