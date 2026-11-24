package com.example.quickhabit.ui.home

import com.example.quickhabit.domain.model.Habit

// P
data class HomeUiState(
    val habits: List<Habit> = emptyList(),
    val allHabits: List<Habit> = emptyList(),

    val selectedFilter: String = "All",
    val doneCount: Int = 0,
    val totalCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val sortByName: Boolean = false
)