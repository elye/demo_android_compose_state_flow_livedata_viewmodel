package com.example.flowexperiment

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LiveDataTransformationActivity: AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

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

class MainViewModel : ViewModel() {

    private val repository = Repository()

    val liveData: LiveData<String> =
        Transformations.map(repository.liveData) {
            Log.d("Track", "ViewModel Map: ${Thread.currentThread().name}")
            it.toString().padStart(8, '0')
        }


    fun trigger() {
        viewModelScope.launch(Dispatchers.Default) {
            repository.getData()
        }
    }
}

class Repository() {
    private val _liveData = MutableLiveData(0)
    val liveData: LiveData<Int> = _liveData

    fun getData() {
        Log.d("Track", "Repository Fetch: ${Thread.currentThread().name}")
        _liveData.postValue((1..1000).random())
    }
}
