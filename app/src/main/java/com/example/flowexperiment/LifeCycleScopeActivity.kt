package com.example.flowexperiment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LifeCycleScopeActivity: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instantiate directly instead of using by ViewModel()
        // So that it will start immediately, instead of start lazily.
        val viewModel = ViewModelProvider(this)
            .get(LifeCycleScopeViewModel::class.java)

        lifecycleScope.launchWhenStarted {
            var launchWhenStarted = 0
            // Do something
            repeat(10) {
                delay(1000)
                Log.d("Track", "Launch when launchWhenStarted ${++launchWhenStarted}")
            }
        }

        ScreenlessDialogFragment().show(supportFragmentManager)

        lifecycleScope.launch {
            whenStarted {
                var whenStarted = 0

                repeat(10) {
                    delay(1000)
                    Log.d("Track", "Launch when whenStarted ${++whenStarted}")
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                var repeatOnLifecycleStarted = 0
                repeat(10) {
                    delay(1000)
                    Log.d("Track", "Launch when repeatOnLifecycleStarted ${++repeatOnLifecycleStarted}")
                }
            }
        }
    }

    class ScreenlessDialogFragment : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(requireContext())
                .setMessage("This is a screen-less activity")
                .setPositiveButton("Okay") { _,_ -> }
                .create()

        companion object {
            const val TAG = "ScreenlessDialogFragment"
        }

        fun show(fragmentManger: FragmentManager) {
            show(fragmentManger, TAG)
        }
    }
}
