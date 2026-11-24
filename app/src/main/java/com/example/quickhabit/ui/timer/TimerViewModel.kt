package com.example.quickhabit.ui.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickhabit.notification.NotificationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class TimerViewModel(private val notifier: NotificationHelper) : ViewModel() {

    private val _state = MutableStateFlow<TimerUiState>(TimerUiState.Idle)
    val state: StateFlow<TimerUiState> = _state.asStateFlow()

    private var timerJob: Job? = null

    fun start(habitName: String, seconds: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            for (left in seconds downTo 1) {
                _state.value = TimerUiState.Running(left)
                delay(1000)
            }
            _state.value = TimerUiState.Finished
            notifier.showTimerFinished(habitName)
        }
    }

    fun cancel() {
        timerJob?.cancel()
        _state.value = TimerUiState.Idle
    }
}