package com.example.quickhabit.data.repository

import com.example.quickhabit.data.remote.HabitApi
import com.example.quickhabit.data.remote.HabitDto
import com.example.quickhabit.domain.model.Habit
import com.example.quickhabit.domain.repository.HabitRepository
// R4
// D
class HabitRepositoryImpl(
    private val api: HabitApi
) : HabitRepository {

    private var cache: List<Habit> = emptyList()

    override suspend fun getHabits(): List<Habit> {
        val dtos = api.getTodos()
        cache = dtos.map { it.toHabit() }
        return cache
    }

    override suspend fun setDone(id: Int, done: Boolean) {
        cache = cache.map { if (it.id == id) it.copy(doneToday = done) else it }
    }
}

private fun HabitDto.toHabit(): Habit = Habit(
    id = id,
    name = title.replaceFirstChar { it.uppercase() },
    description = "Loaded from the server",
    streak = 0,
    reminderTime = "09:00",
    doneToday = completed
)