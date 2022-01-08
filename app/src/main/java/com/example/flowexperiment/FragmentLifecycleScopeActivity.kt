package com.example.flowexperiment

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import androidx.fragment.app.commit
import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentLifecycleScopeActivity :
    AppCompatActivity(R.layout.activity_fragment_lifecycle_scope) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            createFirstFragment()
        }
    }

    fun createFirstFragment() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<FirstFragment>(R.id.fragment_container_view)
            addToBackStack(FirstFragment.TAG)
        }
    }

    fun createSecondFragment() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<SecondFragment>(R.id.fragment_container_view)
            addToBackStack(SecondFragment.TAG)
        }
    }
}

class FirstFragment : Fragment(R.layout.fragment_layout) {
    companion object {
        const val TAG = "First Fragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.my_button).apply {
            text = TAG
            setOnClickListener {
                (activity as FragmentLifecycleScopeActivity).createSecondFragment()
            }
        }
    }
}

class SecondFragment : Fragment(R.layout.fragment_layout) {
    companion object {
        const val TAG = "Second Fragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Elisha", "Second Fragment onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Elisha", "Second Fragment onDestroy")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("Elisha", "Second Fragment onAttach")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("Elisha", "Second Fragment onDetach")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Elisha", "Second Fragment onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Elisha", "Second Fragment onPause")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("Elisha", "Second Fragment onCreateView")

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                repeat(100) {
                    Log.d("TrackViewLifecycleOwnerRepeatResume", "Second Fragment $it")
                    delay(1000)
                }
            }
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Elisha", "Second Fragment onDestroyView")
    }

    init {
        lifecycleScope.launch {
            whenResumed {
                repeat(100) {
                    Log.d("TrackLifecycleScopeWhenResume", "Second Fragment $it")
                    delay(1000)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("Elisha", "Second Fragment onViewCreated")

        view.findViewById<Button>(R.id.my_button).apply {
            text = TAG
            setOnClickListener {
                (activity as FragmentLifecycleScopeActivity).createFirstFragment()
            }
        }
    }
}
