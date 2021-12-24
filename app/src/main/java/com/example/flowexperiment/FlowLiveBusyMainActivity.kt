package com.example.flowexperiment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import com.example.flowexperiment.ui.theme.FlowExperimentTheme

class FlowLiveBusyMainActivity : ComponentActivity() {

    private val viewModel: FlowLiveBusyMainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlowExperimentTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Render(viewModel)
                }
            }
        }
    }

    @Composable
    fun Render(viewModel: FlowLiveBusyMainViewModel) {

        val liveDataPostResult = viewModel.liveDataPost.observeAsState()
        val liveDataSetWithContextResult = viewModel.liveDataSetWithContext.observeAsState()
        val liveDataSetLaunchResult = viewModel.liveDataSetLaunch.observeAsState()
        val liveDataFlowResult = viewModel.stateFlow.collectAsState()

        Column {
            Button(onClick = { viewModel.triggerStateFlow() }) {
                Text(text = "Click Me!")
            }
            Text(text = "StateFlow: ${liveDataFlowResult.value}")

            Button(onClick = { viewModel.triggerLivePostData() }) {
                Text(text = "Click Me!")
            }
            Text(text = "LiveData Post ${liveDataPostResult.value}")

            Button(onClick = { viewModel.triggerLiveSetWithContextData() }) {
                Text(text = "Click Me!")
            }
            Text(text = "LiveData Set WithContext ${liveDataSetWithContextResult.value}")

            Button(onClick = { viewModel.triggerLiveSetLaunchData() }) {
                Text(text = "Click Me!")
            }
            Text(text = "LiveData Set Launch ${liveDataSetLaunchResult.value}")
        }
    }
}
