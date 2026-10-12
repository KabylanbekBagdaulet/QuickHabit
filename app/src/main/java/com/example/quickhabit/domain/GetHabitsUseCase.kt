package com.example.quickhabit.domain
import com.example.quickhabit.data.Habit
import com.example.quickhabit.data.HabitRepository

class GetHabitsUseCase(private val repository: HabitRepository)  {
    suspend operator fun invoke(): List<Habit> {
            return repository.getHabits()
    }
}