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

object AssetUtils {

    private const val TAG = "AssetUtils"

    /**
     * Memuat file model .pt dari assets dan menyalinnya ke direktori cache internal.
     * Ini perlu karena PyTorch tidak bisa langsung membaca file dari assets.
     */
    fun loadModel(context: Context, assetName: String): Module {
        try {
            val file = assetFilePath(context, assetName)
            Log.d(TAG, "✅ Model file ditemukan di: $file")
            return Module.load(file)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Gagal memuat model dari asset: ${e.message}")
            throw e
        }
    }

    /**
     * Memuat label file (.npy atau .txt) dari assets dan mengubahnya ke dalam List<String>.
     */
    fun loadLabels(context: Context, assetName: String): List<String> {
        return try {
            val file = assetFilePath(context, assetName)
            val extension = assetName.substringAfterLast('.', "")
            val labels: List<String>

            if (extension == "txt") {
                // Format sederhana: setiap baris satu label
                labels = context.assets.open(assetName)
                    .bufferedReader()
                    .useLines { it.toList() }
            } else if (extension == "npy") {
                // Format NumPy: diasumsikan label disimpan dalam bentuk string array
                val bytes = Files.readAllBytes(File(file).toPath())
                val content = decodeNpyToStrings(bytes)
                labels = content
            } else {
                throw IllegalArgumentException("Format label tidak dikenali: .$extension")
            }

            Log.d(TAG, "✅ Label berhasil dimuat (${labels.size} kelas): $labels")
            labels
        } catch (e: Exception) {
            Log.e(TAG, "❌ Gagal memuat label: ${e.message}")
            emptyList()
        }
    }

    /**
     * Helper — menyalin file dari assets ke storage internal.
     */
    private fun assetFilePath(context: Context, assetName: String): String {
        val file = File(context.filesDir, assetName)

        if (file.exists() && file.length() > 0) {
            Log.d(TAG, "ℹ️ Asset sudah ada di cache: ${file.absolutePath}")
            return file.absolutePath
        }

        context.assets.open(assetName).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                val buffer = ByteArray(4096)
                var read: Int
                while (true) {
                    read = inputStream.read(buffer)
                    if (read == -1) break
                    outputStream.write(buffer, 0, read)
                }
                outputStream.flush()
            }
        }

        Log.d(TAG, "📦 Asset disalin ke internal: ${file.absolutePath}")
        return file.absolutePath
    }

    /**
     * Decoder sederhana untuk file .npy (NumPy string array).
     * Hanya mendukung array 1D of strings.
     */
    private fun decodeNpyToStrings(bytes: ByteArray): List<String> {
        try {
            val headerEnd = String(bytes).indexOf("]")
            val header = String(bytes.copyOfRange(0, headerEnd + 1))
            val start = headerEnd + 1
            val payload = bytes.copyOfRange(start, bytes.size)

            // Heuristik sederhana — ubah byte menjadi teks dan pisahkan label
            val payloadText = String(payload, Charsets.UTF_8)
            return payloadText
                .replace("[", "")
                .replace("]", "")
                .replace("'", "")
                .replace("\"", "")
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error parsing .npy file: ${e.message}")
            return emptyList()
        }
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
