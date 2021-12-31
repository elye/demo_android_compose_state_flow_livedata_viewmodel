package com.example.flowexperiment

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class LiveDataCoroutineBuilderActivity : AppCompatActivity() {

    private val viewModel: LiveDataCoroutineBuilderViewModel by viewModels()

    private val changeObserver = Observer<String> { value ->
        value?.let {
            Log.d("Track", "Activity Display: ${Thread.currentThread().name}")
            findViewById<Button>(R.id.my_button).text = it
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_livedatatransformation)

        viewModel.liveData.observe(this, changeObserver)

        findViewById<Button>(R.id.my_button).setOnClickListener {
            viewModel.trigger()
        }
    }
}

class LiveDataCoroutineBuilderViewModel : ViewModel() {

    private val repository = LiveDataCoroutineBuilderRepository(viewModelScope.coroutineContext)

    val liveData: LiveData<String> =
        Transformations.switchMap(repository.liveData) {
            liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
                Log.d("Track", "ViewModel Map: ${Thread.currentThread().name}")
                emit(it.toString().padStart(8, '0'))
            }
        }

    fun trigger() {
        viewModelScope.launch(Dispatchers.Default) {
            repository.getData()
        }
    }
}

class LiveDataCoroutineBuilderRepository(coroutineContext: CoroutineContext) {
    private val _stateFlow = MutableStateFlow(0)

    val liveData: LiveData<Int> = _stateFlow.asLiveData(coroutineContext + Dispatchers.IO)

    fun getData() {
        Log.d("Track", "Repository Fetch: ${Thread.currentThread().name}")
        _stateFlow.value++
    }
}
