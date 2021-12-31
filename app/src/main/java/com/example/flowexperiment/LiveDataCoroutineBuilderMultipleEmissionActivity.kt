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

class LiveDataCoroutineBuilderMultipleEmissionActivity : AppCompatActivity() {

    private val viewModel: LiveDataCoroutineBuilderMultipleEmissionViewModel by viewModels()

    private val changeObserver = Observer<String> { value ->
        value?.let {
            Log.d("Track", "Activity Display: ${Thread.currentThread().name}")
            findViewById<Button>(R.id.my_button).text = it
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_livedatatransformation)

        findViewById<Button>(R.id.my_button).setOnClickListener {
            viewModel.trigger().observe(this, changeObserver)
        }
    }
}

class LiveDataCoroutineBuilderMultipleEmissionViewModel : ViewModel() {

    private val repository =
        LiveDataCoroutineBuilderMultipleEmissionRepository(viewModelScope.coroutineContext)

    fun trigger() = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emitSource(repository.liveDataSourceA)
        delay(2000)
        emitSource(repository.liveDataSourceB)
        delay(2000)
        emitSource(repository.liveDataSourceC)
    }
}

class LiveDataCoroutineBuilderMultipleEmissionRepository(val coroutineContext: CoroutineContext) {

    val liveDataSourceA: LiveData<String>
    get() = liveData(coroutineContext + Dispatchers.IO) {
        Log.d("Track", "Repository 2 Fetch: ${Thread.currentThread().name}")
        emit((0..9).random().toString())
    }
    val liveDataSourceB: LiveData<String>
    get() = liveData(coroutineContext + Dispatchers.IO) {
        Log.d("Track", "Repository 2 Fetch: ${Thread.currentThread().name}")
        emit((10..99).random().toString())
    }
    val liveDataSourceC: LiveData<String>
    get() = liveData(coroutineContext + Dispatchers.IO) {
        Log.d("Track", "Repository 3 Fetch: ${Thread.currentThread().name}")
        emit((100..999).random().toString())
    }
}
