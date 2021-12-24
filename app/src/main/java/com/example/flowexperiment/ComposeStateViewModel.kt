package com.example.flowexperiment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import kotlinx.coroutines.*

class ComposeStateViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    var mutableComposeState by mutableStateOf(0)
        private set

    var saveableMutableComposeState
            by SaveableComposeState(savedStateHandle, "ComposeKey", 0)
        private set

    fun triggerComposeState() {
        ++mutableComposeState
    }

    fun triggerSaveableComposeState() {
        ++saveableMutableComposeState
    }
}
