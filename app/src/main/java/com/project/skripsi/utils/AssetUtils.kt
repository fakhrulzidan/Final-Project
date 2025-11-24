package com.project.skripsi.utils

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.pytorch.Module
import org.pytorch.Tensor
import java.io.File
import java.io.FileOutputStream

object PytorchModelLoader {

    fun load(context: Context, modelName: String): Module {
        val filePath = assetFilePath(context, modelName)
        return Module.load(filePath)
    }

    private fun assetFilePath(context: Context, assetName: String): String {
        val file = File(context.filesDir, assetName)
        if (file.exists() && file.length() > 0) {
            return file.absolutePath
        }

        context.assets.open(assetName).use { input ->
            FileOutputStream(file).use { output ->
                val buffer = ByteArray(4096)
                var read = input.read(buffer)
                while (read != -1) {
                    output.write(buffer, 0, read)
                    read = input.read(buffer)
                }
            }
        }
        return file.absolutePath
    }
}