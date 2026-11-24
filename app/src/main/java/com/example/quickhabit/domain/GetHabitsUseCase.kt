package com.example.quickhabit.domain
import com.example.quickhabit.domain.model.Habit
import com.example.quickhabit.domain.repository.HabitRepository
class GetHabitsUseCase(private val repository: HabitRepository)  {
    suspend operator fun invoke(): List<Habit> {
            return repository.getHabits()
    }
}