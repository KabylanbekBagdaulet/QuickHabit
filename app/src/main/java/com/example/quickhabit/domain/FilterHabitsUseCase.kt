package com.example.quickhabit.domain
import com.example.quickhabit.data.Habit
class FilterHabitsUseCase {
    operator fun invoke(habits: List<Habit>, filter: String): List<Habit> = when (filter){
        "Done" -> habits.filter { it.doneToday }
        "Not done" -> habits.filter { !it.doneToday }
        else -> habits
    }

}