package com.example.quickhabit.domain.repository
import com.example.quickhabit.domain.model.Habit
// R4
interface HabitRepository {

    suspend fun getHabits(): List<Habit>

    suspend fun setDone(id: Int, done: Boolean)
}