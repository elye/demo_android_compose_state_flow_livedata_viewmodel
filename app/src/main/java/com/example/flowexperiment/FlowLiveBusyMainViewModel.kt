package com.example.flowexperiment

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FlowLiveBusyMainViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _liveDataPost =
        SafeMutableLiveData(savedStateHandle, "LivePostKey", 0)

    val liveDataPost: LiveData<Int> = _liveDataPost

    private val _liveDataSetWithContext =
        SafeMutableLiveData(savedStateHandle, "LiveSetWithContextKey", 0)

    val liveDataSetWithContext: LiveData<Int> = _liveDataSetWithContext

    private val _liveDataSetLaunch =
        SafeMutableLiveData(savedStateHandle, "LiveSetLaunchKey", 0)

    val liveDataSetLaunch: LiveData<Int> = _liveDataSetLaunch

    private val _stateFlow =
        SaveableMutableSaveStateFlow(savedStateHandle, "FlowKey", 0)

    val stateFlow = _stateFlow.asStateFlow()


    fun triggerLivePostData() {
        viewModelScope.launch(Dispatchers.Default) {
            repeat(10) {
                delay(1000)
                _liveDataPost.postValue(_liveDataPost.value + 1)
            }
        }
        Thread.sleep(3000)
    }

    fun triggerLiveSetWithContextData() {
        viewModelScope.launch(Dispatchers.Default) {
            repeat(10) {
                delay(1000)
                withContext(Dispatchers.Main) {
                    ++_liveDataSetWithContext.value
                }
            }
        }
        Thread.sleep(3000)
    }

    fun triggerLiveSetLaunchData() {
        viewModelScope.launch(Dispatchers.Default) {
            repeat(10) {
                delay(1000)
                launch(Dispatchers.Main) {
                    ++_liveDataSetLaunch.value
                }
            }
        }
        Thread.sleep(3000)
    }

    fun triggerStateFlow() {
        viewModelScope.launch(Dispatchers.Default) {
            repeat(10) {
                delay(1000)
                ++_stateFlow.value
            }
        }
        Thread.sleep(3000)
    }
}
