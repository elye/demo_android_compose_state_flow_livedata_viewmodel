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
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.flowexperiment.ui.theme.FlowExperimentTheme

class ComposeStateActivity : ComponentActivity() {

    private val viewModel: ComposeStateViewModel by viewModels()

    private var state by mutableStateOf(0)

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
    fun Render(viewModel: ComposeStateViewModel) {

        var rememberState by remember {
            mutableStateOf(0)
        }
        var rememberSaveableState by rememberSaveable {
            mutableStateOf(0)
        }

        Column {
            Button(onClick = { state++ }) {
                Text(text = "Click Me!")
            }
            Text(text = "Compose State: $state")

            Button(onClick = { rememberState++ }) {
                Text(text = "Click Me!")
            }
            Text(text = "Remember Compose State: $rememberState")

            Button(onClick = { rememberSaveableState++ }) {
                Text(text = "Click Me!")
            }
            Text(text = "RememberSaveable Compose State: $rememberSaveableState")

            Button(onClick = { viewModel.triggerComposeState() }) {
                Text(text = "Click Me!")
            }
            Text(text = "ViewModel Compose State: ${viewModel.mutableComposeState}")

            Button(onClick = { viewModel.triggerSaveableComposeState() }) {
                Text(text = "Click Me!")
            }
            Text(text = "ViewModel Saveable Compose State: ${viewModel.saveableMutableComposeState}")
        }
    }
}
