package com.project.skripsi.utils

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.pytorch.Module
import org.pytorch.Tensor
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.Files

// ======================================================
// ==============  LOAD MODEL FILE (.pt)  ===============
// ======================================================

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
//fun assetFilePath(context: Context, assetName: String): String {
//    val file = File(context.filesDir, assetName)
//    try {
//        context.assets.open(assetName).use { inputStream ->
//            FileOutputStream(file).use { outputStream ->
//                val buffer = ByteArray(4096)
//                var read: Int
//                while (true) {
//                    read = inputStream.read(buffer)
//                    if (read == -1) break
//                    outputStream.write(buffer, 0, read)
//                }
//                outputStream.flush()
//            }
//        }
//    } catch (e: Exception) {
//        Log.e("AssetUtils", "Error copying asset file: ${e.message}")
//    }
//    return file.absolutePath
//}
//
//// ======================================================
//// ==============  LOAD NUMPY FILE (.npy)  ==============
//// ======================================================
//
//fun loadNpyAsset(context: Context, fileName: String): FloatArray {
//    return try {
//        val inputStream = context.assets.open(fileName)
//        val bytes = inputStream.readBytes()
//        inputStream.close()
//
//        // Cari header numpy
//        val headerEnd = bytes.indexOf(0x0A.toByte()) + 1
//        val dataBytes = bytes.copyOfRange(headerEnd, bytes.size)
//
//        // Pastikan urutan byte sesuai little-endian
//        val byteBuffer = ByteBuffer.wrap(dataBytes).order(ByteOrder.LITTLE_ENDIAN)
//        val floatArray = FloatArray(dataBytes.size / 4)
//        byteBuffer.asFloatBuffer().get(floatArray)
//        floatArray
//    } catch (e: Exception) {
//        Log.e("AssetUtils", "Error loading npy asset $fileName: ${e.message}")
//        FloatArray(0)
//    }
//}
//
//// ======================================================
//// ==============  LOAD LABELS (JSON or NPY)  ===========
//// ======================================================
//
//fun loadLabelClasses(context: Context, fileName: String): Array<String> {
//    return try {
//        val inputStream = context.assets.open(fileName)
//        val bytes = inputStream.readBytes()
//        inputStream.close()
//
//        // Jika file JSON
//        if (fileName.endsWith(".json")) {
//            val jsonArray = JSONArray(String(bytes))
//            Array(jsonArray.length()) { i -> jsonArray.getString(i) }
//        }
//        // Jika file NPY (berisi array of string)
//        else if (fileName.endsWith(".npy")) {
//            val content = String(bytes)
//            content.replace("[", "")
//                .replace("]", "")
//                .replace("'", "")
//                .split(",")
//                .map { it.trim() }
//                .toTypedArray()
//        } else {
//            arrayOf("Unknown")
//        }
//    } catch (e: Exception) {
//        Log.e("AssetUtils", "Error loading labels: ${e.message}")
//        arrayOf("Unknown")
//    }
//}
