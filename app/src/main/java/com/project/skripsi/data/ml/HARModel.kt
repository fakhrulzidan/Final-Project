package com.project.skripsi.data.ml

import android.content.Context
import org.pytorch.IValue
//import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import org.pytorch.Tensor

object HarModelConfig {

    // Match training StandardScaler
    val mean = floatArrayOf(
        0.85641652f,
        8.81660203f,
        1.45683444f
    )

    val scale = floatArrayOf(
        3.69520385f,
        4.86642237f,
        3.24596981f
    )

    // Match label_encoder.classes_
    val labels = arrayOf(
        "Downstairs",
        "Jogging",
        "Sitting",
        "Standing",
        "Upstairs",
        "Walking"
    )
}