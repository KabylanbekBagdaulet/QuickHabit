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
import com.example.quickhabit.domain.FilterHabitsUseCase
import com.example.quickhabit.domain.GetHabitsUseCase
import com.example.quickhabit.domain.ToggleHabitUseCase

class HomeViewModel : ViewModel() {
    private val repository = HabitRepository()
    private val getHabits = GetHabitsUseCase(repository)
    private val toggleHabit = ToggleHabitUseCase(repository)
    private val filterHabits = FilterHabitsUseCase()
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
                allHabits = getHabits()
                refresh()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Unknown error")
                }
            }
        }

    }
    fun onFilterSelected(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter, habits = filterHabits(allHabits, filter)) }
    }
    fun onToggleHabit(id: Int, done: Boolean) {
        allHabits = allHabits.map { if (it.id == id) it.copy(doneToday = done) else it }
        refresh()
        viewModelScope.launch { toggleHabit(id, done) }
    }
    fun onRetry() {
        repository.shouldFail = false
        loadHabits()
    }

    private fun refresh() {
        _uiState.update {
            it.copy(
                habits = filterHabits(allHabits, it.selectedFilter),
                allHabits = allHabits,
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