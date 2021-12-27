package com.example.flowexperiment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LifeCycleScopeViewModel : ViewModel() {

    init {
        viewModelScope.launch {
            var viewModelScopeCount = 0

            repeat(10) {
                delay(1000)
                Log.d("Track", "Launch when viewModelScope ${++viewModelScopeCount}")
            }
        }
    }
}
