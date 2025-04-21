package com.nc.torch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlin.random.Random

class RandomViewModel : ViewModel() {

    private val _randomFlow = MutableStateFlow(0)
    val randomFlow: StateFlow<Int> get() = _randomFlow
    private var generationJob: Job? = null
    val isBlinking = MutableStateFlow(false)
    val blinkDelay = MutableStateFlow(100L)

    fun startGeneration(size: Int) {
        generationJob?.cancel()  // Cancel previous if exists

        generationJob = viewModelScope.launch {
            infiniteRandomFlow(size)
                .flowOn(Dispatchers.Default)  // Background thread
                .collect { index ->
                    _randomFlow.value = index
                }
        }
    }

    fun stopGeneration() {
        generationJob?.cancel()
    }

    private fun infiniteRandomFlow(size: Int) = flow {
        val random = Random(System.currentTimeMillis())
        while (true) {
            emit(random.nextInt(0, size))
            delay(50)
        }
    }
}