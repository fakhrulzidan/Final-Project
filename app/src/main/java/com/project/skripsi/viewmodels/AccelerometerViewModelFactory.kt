package com.project.skripsi.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.skripsi.viewmodels.AccelerometerViewModel

class AccelerometerViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccelerometerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AccelerometerViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}