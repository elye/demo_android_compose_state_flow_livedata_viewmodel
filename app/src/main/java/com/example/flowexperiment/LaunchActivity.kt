package com.example.flowexperiment

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.*
import com.example.flowexperiment.ui.theme.FlowExperimentTheme

class LaunchActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FlowExperimentTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Column {
                        Spacer(Modifier.height(10.dp))
                        Button(onClick = {
                            startActivity(
                                Intent(this@LaunchActivity, ComposeStateActivity::class.java)
                            )
                        }) {
                            Text("Launch Compose State Experiment Activity")
                        }
                        Spacer(Modifier.height(10.dp))
                        Button(onClick = {
                            startActivity(
                                Intent(this@LaunchActivity, FlowLiveBusyMainActivity::class.java)
                            )
                        }) {
                            Text("Launch Flow vs Live Busy Main Experiment Activity")
                        }
                        Spacer(Modifier.height(10.dp))
                        Button(onClick = {
                            startActivity(
                                Intent(this@LaunchActivity, LifeCycleScopeActivity::class.java)
                            )
                        }) {
                            Text("Launch Lifecycle Scope Experiment Activity")
                        }
                        Spacer(Modifier.height(10.dp))
                        Button(onClick = {
                            startActivity(
                                Intent(this@LaunchActivity, LiveDataTransformationActivity::class.java)
                            )
                        }) {
                            Text("Launch Livedata Transformation Experiment Activity")
                        }
                        Spacer(Modifier.height(10.dp))
                        Button(onClick = {
                            startActivity(
                                Intent(this@LaunchActivity, LiveDataCoroutineBuilderActivity::class.java)
                            )
                        }) {
                            Text("Launch Livedata Coroutine Builder Experiment Activity")
                        }
                        Spacer(Modifier.height(10.dp))
                        Button(onClick = {
                            startActivity(
                                Intent(this@LaunchActivity, LiveDataCoroutineBuilderMultipleEmissionActivity::class.java)
                            )
                        }) {
                            Text("Launch Livedata Coroutine Builder Multiple Emission Experiment Activity")
                        }
                    }
                }
            }
        }
    }
}
