package com.example.quickhabit.domain

import com.example.quickhabit.data.HabitRepository

class ToggleHabitUseCase (private val repository: HabitRepository) {
    suspend operator fun invoke(id: Int, done: Boolean) {
        repository.setDone(id, done)
    }
}