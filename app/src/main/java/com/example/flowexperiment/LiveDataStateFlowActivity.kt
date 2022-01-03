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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class LiveDataStateFlowActivity : AppCompatActivity() {

    private val viewModel: LiveDataStateFlowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_livedata_stateflow)

        viewModel.liveData.observe(this) {
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

        viewModel.liveDataTrigger.observe(this) {
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

class LiveDataStateFlowViewModel : ViewModel() {

    private val repository = LiveDataStateFlowRepository()

    val liveData: LiveData<String> = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        val data = repository.loadDataLive()
        Log.d("TrackLoadLiveData", "ViewModel ($data): ${Thread.currentThread().name}")
        emit(data)
    }

    val stateFlow = flow {
        val data = repository.loadDataState()
        Log.d("TrackLoadStateFlow", "ViewModel ($data): ${Thread.currentThread().name}")
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
        .triggerState
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

class LiveDataStateFlowRepository {
    val triggerLive = MutableLiveData("Initial")
    val triggerState = MutableStateFlow("Initial")

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

    fun getLiveData() {
        val value = generateRandom()
        Log.d("TrackTriggerLiveData", "Repository ($value): ${Thread.currentThread().name}")
        triggerLive.postValue(value)
    }

    fun getStateData() {
        val value = generateRandom()
        Log.d("TrackTriggerStateFlow", "Repository ($value): ${Thread.currentThread().name}")
        triggerState.value = value
    }
}
