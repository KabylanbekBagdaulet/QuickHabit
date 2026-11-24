package com.example.quickhabit.domain

import com.example.quickhabit.domain.repository.HabitRepository
class ToggleHabitUseCase (private val repository: HabitRepository) {
    suspend operator fun invoke(id: Int, done: Boolean) {
        repository.setDone(id, done)
    }
}