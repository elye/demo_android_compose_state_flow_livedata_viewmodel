package com.example.flowexperiment

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.lifecycle.*
import com.example.flowexperiment.ui.theme.FlowExperimentTheme

class LaunchActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FlowExperimentTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Column {
                        Button(onClick = {
                            startActivity(
                                Intent(this@LaunchActivity, ComposeStateActivity::class.java)
                            )
                        }) {
                            Text("Launch Compose State Experiment Activity")
                        }
                        Button(onClick = {
                            startActivity(
                                Intent(this@LaunchActivity, FlowLiveBusyMainActivity::class.java)
                            )
                        }) {
                            Text("Launch Flow vs Live Busy Main Experiment Activity")
                        }
                        Button(onClick = {
                            startActivity(
                                Intent(this@LaunchActivity, LifeCycleScopeActivity::class.java)
                            )
                        }) {
                            Text("Launch Lifecycle Scope Experiment Activity")
                        }
                    }
                }
            }
        }
    }
}
