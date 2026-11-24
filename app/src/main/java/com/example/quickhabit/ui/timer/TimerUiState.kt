package com.example.quickhabit.ui.timer

sealed interface TimerUiState {
    data object Idle : TimerUiState
    data class Running(val secondsLeft: Int) : TimerUiState
    data object Finished : TimerUiState
}