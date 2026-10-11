package com.example.quickhabit.data

import kotlinx.coroutines.delay

class HabitRepository{

    private val items = habits.toMutableSet()

    var shouldFail = false

    suspend fun gethabits(): List<Habit> {
        delay(1500)
        if(shouldFail) throw Exception("Something went wrong")
        return items.toList()
    }
    suspend fun setDone(id: Int, done: Boolean){
        val index = items.indexOfFirst { it.id == id }
        if(index >= 0) items[index] = items[index].copy(doneToday = done)
    }

}