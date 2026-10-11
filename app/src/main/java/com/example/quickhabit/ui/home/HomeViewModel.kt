package com.example.quickhabit.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickhabit.data.Habit
import com.example.quickhabit.data.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val repository = HabitRepository()
    private var allHabits: List<Habit> = emptyList()
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        Log.d("HomeViewModel", "init")
        loadHabits()
    }
    private fun loadHabits() {
        _uiState.update {it.copy(isLoading = true, error = null)}
        viewModelScope.launch {
            try {
                allHabits = repository.gethabits()
                refresh()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Unknown error")
                }
            }
        }

    }
    fun onFilterSelected(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter, habits = applyFilter(filter)) }
    }
    fun onToggleHabit(id: Int, done: Boolean) {
        allHabits = allHabits.map { if (it.id == id) it.copy(doneToday = done) else it }
        refresh()
        viewModelScope.launch { repository.setDone(id, done) }
    }
    fun onRetry() {
        repository.shouldFail = false
        loadHabits()
    }
    private fun applyFilter(filter: String): List<Habit> = when (filter) {
        "Done" -> allHabits.filter { it.doneToday }
        "Not done" -> allHabits.filter { !it.doneToday }
        else -> allHabits
    }
    private fun refresh() {
        _uiState.update {
            it.copy(
                habits = applyFilter(it.selectedFilter),
                doneCount = allHabits.count { h -> h.doneToday },
                totalCount = allHabits.size,
                isLoading = false,
                error = null
            )
        }
    }
    override fun onCleared() {
        super.onCleared()
        Log.d("HomeViewModel", "onCleared")
    }

}