# QuickHabit

A minimalist Android habit tracker that lets users add daily habits, mark them complete, and track streaks over time.

## Functionality
1. Create a new habit with a name and optional reminder time
2. Edit an existing habit
3. Delete a habit
4. View a list of all saved habits
5. Mark a habit as done for the current day
6. View current streak per habit
7. Data persists locally on the device
8. Empty-state screen shown when no habits exist
9. Habit Detail screen with streak history
10. Settings screen to toggle daily reminders

## Folder Structure
```text
app/
 ├── src/main/java/com/example/quickhabit/
 │   ├── MainActivity.kt
 │   ├── ui/
 │   │   ├── HomeScreen.kt
 │   │   ├── AddEditHabitScreen.kt
 │   │   ├── HabitDetailScreen.kt
 │   │   └── SettingsScreen.kt
 │   └── data/
 │       ├── Habit.kt
 │       ├── HabitDatabase.kt
 │       └── HabitRepository.kt
 └── src/main/res/
     ├── layout/
     └── values/
build.gradle.kts
gradle.properties
gradlew
gradlew.bat
settings.gradle.kts
.gitignore
README.md
