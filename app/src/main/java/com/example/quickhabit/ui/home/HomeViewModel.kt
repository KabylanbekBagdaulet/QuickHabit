package com.example.quickhabit.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickhabit.domain.model.Habit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.quickhabit.domain.FilterHabitsUseCase
import com.example.quickhabit.domain.GetHabitsUseCase
import com.example.quickhabit.domain.ToggleHabitUseCase
import java.io.IOException

class HomeViewModel(
    private val getHabits: GetHabitsUseCase,
    private val toggleHabit: ToggleHabitUseCase,
    private val filterHabits: FilterHabitsUseCase
) : ViewModel() {
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
                // R1
                allHabits = getHabits()
                refresh()

                // r3
            } catch (e: IOException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "No connection. Check your internet and try again."
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Could not load habits"
                    )
                }
            }
        }
    }
    fun onFilterSelected(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter, habits = filterHabits(allHabits, filter)) }
    }
    fun onToggleHabit(id: Int, done: Boolean) {
        allHabits = allHabits.map { if (it.id == id) it.copy(doneToday = done) else it }
        // r2
        refresh()
        viewModelScope.launch { toggleHabit(id, done) }
    }
    fun onRetry() {
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