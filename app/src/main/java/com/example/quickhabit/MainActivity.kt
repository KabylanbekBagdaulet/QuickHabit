package com.example.quickhabit

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.quickhabit.data.Habit
import com.example.quickhabit.data.filters
import com.example.quickhabit.data.habits
import com.example.quickhabit.ui.theme.QuickHabitTheme
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var darkTheme by rememberSaveable { mutableStateOf(false) }
            val systemDark = isSystemInDarkTheme()
            var initialized by rememberSaveable { mutableStateOf(false) }
            if (!initialized) {
                darkTheme = systemDark
                initialized = true
            }
            QuickHabitTheme(darkTheme = darkTheme) {
                QuickHabitApp(
                    darkTheme = darkTheme,
                    onDarkThemeChange = { darkTheme = it }
                )
            }
        }
    }
}
// ---- Navigation ----
@Composable
fun QuickHabitApp(darkTheme: Boolean, onDarkThemeChange: (Boolean) -> Unit) {
    val navController = rememberNavController()
    val habitList = remember { mutableStateListOf<Habit>().apply { addAll(habits) } }

    fun toggle(id: Int, isDone: Boolean) {
        val index = habitList.indexOfFirst { it.id == id }
        if (index >= 0) habitList[index] = habitList[index].copy(doneToday = isDone)
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                habitList = habitList,
                onToggle = { id, isDone -> toggle(id, isDone) },
                onHabitClick = { id -> navController.navigate("detail/$id") },
                onSettingsClick = { navController.navigate("settings") }
            )
        }
        composable(
            route = "detail/{habitId}",
            arguments = listOf(navArgument("habitId") { type = NavType.IntType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getInt("habitId")
            val habit = habitList.find { it.id == habitId }
            if (habit != null) {
                HabitDetailScreen(
                    habit = habit,
                    onToggleDone = { toggle(habit.id, it) },
                    onBackClick = { navController.popBackStack() }
                )
            } else {
                EmptyState(
                    title = "Habit not found",
                    subtitle = "It may have been removed",
                    buttonText = "Back",
                    onButtonClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        composable("settings") {
            SettingsScreen(
                darkTheme = darkTheme,
                onDarkThemeChange = onDarkThemeChange,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

// ---- Screens ----

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    habitList: List<Habit>,
    onToggle: (Int, Boolean) -> Unit,
    onHabitClick: (Int) -> Unit,
    onSettingsClick: () -> Unit
) {
    var selected by rememberSaveable { mutableStateOf("All") }

    val filtered = when (selected) {
        "Done" -> habitList.filter { it.doneToday }
        "Not done" -> habitList.filter { !it.doneToday }
        else -> habitList.toList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("QuickHabit") },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            FilterChipRow(
                filters = filters,
                selected = selected,
                onSelect = { selected = it }
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (filtered.isEmpty()) {
                EmptyState(
                    title = "No habits here",
                    subtitle = "Try another filter",
                    buttonText = "Show all",
                    onButtonClick = { selected = "All" },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtered, key = { it.id }) { habit ->
                        HabitCard(
                            habit = habit,
                            checked = habit.doneToday,
                            onCheckedChange = { isChecked -> onToggle(habit.id, isChecked) },
                            onClick = { onHabitClick(habit.id) }
                        )
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(
    habit: Habit,
    onToggleDone: (Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = habit.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_empty_habits),
                contentDescription = "Habit icon",
                modifier = Modifier.size(96.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = habit.name, style = MaterialTheme.typography.headlineMedium)
            Row {
                Text(
                    text = "Streak: ${habit.streak} days",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "•", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Reminder ${habit.reminderTime}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = habit.description, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { onToggleDone(!habit.doneToday) },
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text(if (habit.doneToday) "Done for today" else "Mark as done")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    var reminders by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingRow(
                title = "Daily reminders",
                checked = reminders,
                onCheckedChange = { reminders = it }
            )
            SettingRow(
                title = "Dark theme",
                checked = darkTheme,
                onCheckedChange = onDarkThemeChange
            )
        }
    }
}


// ---- Components ----

@Composable
fun HabitCard(
    habit: Habit,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = checked, onCheckedChange = onCheckedChange)
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Streak: ${habit.streak} days",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun FilterChipRow(
    filters: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            FilterChip(
                selected = filter == selected,
                onClick = { onSelect(filter) },
                label = { Text(filter) }
            )
        }
    }
}

@Composable
fun EmptyState(
    title: String,
    subtitle: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_empty_habits),
            contentDescription = "Empty list illustration",
            modifier = Modifier.size(96.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onButtonClick,
            modifier = Modifier.heightIn(min = 48.dp)
        ) {
            Text(buttonText)
        }
    }
}
@Composable
fun SettingRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}


// ---- Previews ----

// ---- Previews ----

@Preview(showBackground = true)
@Composable
fun HabitCardPreview() {
    QuickHabitTheme {
        HabitCard(
            habit = habits[0],
            checked = habits[0].doneToday,
            onCheckedChange = {},
            onClick = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HabitCardDarkPreview() {
    QuickHabitTheme {
        HabitCard(
            habit = habits[0],
            checked = habits[0].doneToday,
            onCheckedChange = {},
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FilterChipRowPreview() {
    QuickHabitTheme {
        FilterChipRow(filters = filters, selected = "All", onSelect = {})
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyStatePreview() {
    QuickHabitTheme {
        EmptyState(
            title = "No habits yet",
            subtitle = "Add your first one",
            buttonText = "Add",
            onButtonClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    QuickHabitTheme {
        HomeScreen(
            habitList = habits,
            onToggle = { _, _ -> },
            onHabitClick = {},
            onSettingsClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun HomeScreenDarkPreview() {
    QuickHabitTheme {
        HomeScreen(
            habitList = habits,
            onToggle = { _, _ -> },
            onHabitClick = {},
            onSettingsClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HabitDetailPreview() {
    QuickHabitTheme {
        HabitDetailScreen(habit = habits[0], onToggleDone = {}, onBackClick = {})
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun HabitDetailDarkPreview() {
    QuickHabitTheme {
        HabitDetailScreen(habit = habits[0], onToggleDone = {}, onBackClick = {})
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsPreview() {
    QuickHabitTheme {
        SettingsScreen(darkTheme = false, onDarkThemeChange = {}, onBackClick = {})
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun SettingsDarkPreview() {
    QuickHabitTheme {
        SettingsScreen(darkTheme = true, onDarkThemeChange = {}, onBackClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun SettingRowPreview() {
    QuickHabitTheme {
        SettingRow(title = "Daily reminders", checked = true, onCheckedChange = {})
    }
}