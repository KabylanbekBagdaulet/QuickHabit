package com.example.quickhabit.data

data class Habit(
    val id: Int,
    val name: String,
    val description: String,
    val streak: Int,
    val reminderTime: String,
    val doneToday: Boolean
)

val habits = listOf(
    Habit(1, "Drink water", "Drink 8 glasses of water every day.", 5, "09:00", true),
    Habit(2, "Read 20 pages", "Read a book before bed to unwind.", 12, "21:30", false),
    Habit(3, "Morning workout", "At least 15 minutes of exercise after waking up.", 3, "07:00", true),
    Habit(4, "Meditate", "Ten minutes of quiet breathing.", 8, "08:00", false),
    Habit(5, "Study Kotlin", "One small Compose exercise every day.", 20, "18:00", true),
    Habit(6, "Walk 8000 steps", "Walk outside, even in bad weather.", 2, "17:00", false),
    Habit(7, "Write a journal", "Three sentences about the day.", 6, "22:00", false),
    Habit(8, "No sugar", "Skip sweets and sugary drinks.", 1, "12:00", true),
    Habit(9, "Sleep before midnight", "Go to bed before 00:00 to rest well.", 4, "23:00", false),
    Habit(10, "Call family", "Call someone close to you.", 9, "19:00", true)
)

val filters = listOf("All", "Done", "Not done")