package com.example.flowexperiment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class SafeMutableLiveData<T : Any>(
    savedStateHandle: SavedStateHandle,
    key: String,
    defaultValue: T
) : MutableLiveData<T>(savedStateHandle.getLiveData(key, defaultValue).value) {

    private val mutableLiveData: MutableLiveData<T> =
        savedStateHandle.getLiveData(key, defaultValue)

    override fun getValue(): T = super.getValue() as T
    override fun setValue(value: T) {
        super.setValue(value)
        mutableLiveData.value = value
    }

    override fun postValue(value: T) {
        super.postValue(value)
        mutableLiveData.postValue(value)
    }
}

class SaveableMutableSaveStateFlow<T>(
    private val savedStateHandle: SavedStateHandle,
    private val key: String,
    defaultValue: T
) {
    private val _state: MutableStateFlow<T> =
        MutableStateFlow(savedStateHandle.get<T>(key) ?: defaultValue)

    var value: T
        get() = _state.value
        set(value) {
            _state.value = value
            savedStateHandle.set(key, value)
        }

    fun asStateFlow(): StateFlow<T> = _state
}

class SaveableComposeState<T>(
    private val savedStateHandle: SavedStateHandle,
    private val key: String,
    defaultValue: T
) {
    private var _state by mutableStateOf(savedStateHandle.get<T>(key) ?: defaultValue)

    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): T {
        return _state
    }

    operator fun setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: T
    ) {
        _state = value
        savedStateHandle.set(key, value)
    }
}

class MainViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _liveData =
        SafeMutableLiveData(savedStateHandle,"LiveKey", 0)

    val liveData: LiveData<Int> = _liveData

    private val _stateFlow =
        SaveableMutableSaveStateFlow(savedStateHandle, "FlowKey", 0)

    val stateFlow = _stateFlow.asStateFlow()

    var mutableComposeState by mutableStateOf(0)
        private set

    var saveableMutableComposeState
            by SaveableComposeState(savedStateHandle, "ComposeKey", 0)
        private set

    fun triggerLiveData() {
        ++_liveData.value
    }

    fun triggerComposeState() {
        ++mutableComposeState
    }

    fun triggerSaveableComposeState() {
        ++saveableMutableComposeState
    }

    fun triggerStateFlow() {
        ++_stateFlow.value
    }
}
