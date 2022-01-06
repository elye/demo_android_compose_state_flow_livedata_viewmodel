package com.example.flowexperiment

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.switchMap
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class LiveDataStateFlowActivity : AppCompatActivity() {

    private val viewModel: LiveDataStateFlowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_livedata_stateflow)

        viewModel
            .liveData
            .distinctUntilChanged()
            .observe(this) {
                Log.d("TrackLoadLiveData", "Activity ($it): ${Thread.currentThread().name}")
                findViewById<TextView>(R.id.my_text_live).text = it
            }

        lifecycleScope.launchWhenStarted {
            viewModel.stateFlow.flowWithLifecycle(
                this@LiveDataStateFlowActivity.lifecycle,
                Lifecycle.State.STARTED
            )
                .distinctUntilChanged()
                .collect {
                    Log.d("TrackLoadStateFlow", "Activity ($it): ${Thread.currentThread().name}")
                    findViewById<TextView>(R.id.my_text_state).text = it
                }
        }

        viewModel
            .liveDataTrigger
            .distinctUntilChanged()
            .observe(this) {
                Log.d("TrackTriggerLiveData", "Activity ($it): ${Thread.currentThread().name}")
                findViewById<Button>(R.id.my_button_live).text = it
            }

        lifecycleScope.launchWhenStarted {
            viewModel.stateFlowTrigger
                .flowWithLifecycle(
                    this@LiveDataStateFlowActivity.lifecycle,
                    Lifecycle.State.STARTED
                )
                .distinctUntilChanged()
                .collect {
                    Log.d("TrackTriggerStateFlow", "Activity ($it): ${Thread.currentThread().name}")
                    findViewById<Button>(R.id.my_button_state).text = it
                }
        }

        findViewById<Button>(R.id.my_button_live)
            .setOnClickListener { viewModel.triggerLive() }

        findViewById<Button>(R.id.my_button_state)
            .setOnClickListener { viewModel.triggerState() }
    }
}

class LiveDataStateFlowViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val repository = LiveDataStateFlowRepository(
        SafeMutableLiveData(
            savedStateHandle,
            "TriggerLiveDataKey",
            "Initial"
        ),
        SaveableMutableSaveStateFlow(
            savedStateHandle,
            "TriggerStateFlowKey",
            "Initial"
        )
    )

    init {
        if (!savedStateHandle.contains("TriggerLiveDataKey")) triggerLive()
        if (!savedStateHandle.contains("TriggerStateFlowKey")) triggerState()
    }

    val liveData: LiveData<String> = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        suspend fun loading(): String {
            emit("Nothing")
            return repository.loadDataLive()
        }

        val data = savedStateHandle.get("LoadLiveDataKey") ?: loading()
        Log.d("TrackLoadLiveData", "ViewModel ($data): ${Thread.currentThread().name}")
        savedStateHandle.set("LoadLiveDataKey", data)
        emit(data)
    }

    val stateFlow = flow {
        val data = savedStateHandle.get("LoadStateFlowKey") ?: repository.loadDataState()
        Log.d("TrackLoadStateFlow", "ViewModel ($data): ${Thread.currentThread().name}")
        savedStateHandle.set("LoadStateFlowKey", data)
        emit(data)
    }.stateIn(
        scope = viewModelScope + Dispatchers.IO,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = "Nothing"
    )

    val liveDataTrigger: LiveData<String> = repository
        .triggerLive
        .distinctUntilChanged()
        .switchMap {
            liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
                Log.d("TrackTriggerLiveData", "ViewModel ($it): ${Thread.currentThread().name}")
                emit(it)
            }
        }

    val stateFlowTrigger = repository
        .triggerState.asStateFlow()
        .mapLatest {
            Log.d("TrackTriggerStateFlow", "ViewModel ($it): ${Thread.currentThread().name}")
            it
        }.stateIn(
            scope = viewModelScope + Dispatchers.IO,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = "Nothing"
        )

    fun triggerLive() {
        viewModelScope.launch(Dispatchers.Default) {
            repository.getLiveData()
        }
    }

    fun triggerState() {
        viewModelScope.launch(Dispatchers.Default) {
            repository.getStateData()
        }
    }
}

class LiveDataStateFlowRepository(
    liveData: MutableLiveData<String>,
    stateFlow: SaveableMutableSaveStateFlow<String>
) {
    val triggerLive = liveData
    val triggerState = stateFlow

    suspend fun loadDataLive(): String {
        delay(2000)
        val value = generateRandom()
        Log.d("TrackLoadLiveData", "Repository ($value): ${Thread.currentThread().name}")
        return value
    }

    private fun generateRandom() = (1..1000).random().toString().padEnd(4, '0')

    suspend fun loadDataState(): String {
        delay(2000)
        val value = generateRandom()
        Log.d("TrackLoadStateFlow", "Repository ($value): ${Thread.currentThread().name}")
        return value
    }

    suspend fun getLiveData() {
        delay(2000)
        val value = generateRandom()
        Log.d("TrackTriggerLiveData", "Repository ($value): ${Thread.currentThread().name}")
        triggerLive.postValue(value)
    }

    suspend fun getStateData() {
        delay(2000)
        val value = generateRandom()
        Log.d("TrackTriggerStateFlow", "Repository ($value): ${Thread.currentThread().name}")
        triggerState.value = value
    }
}
