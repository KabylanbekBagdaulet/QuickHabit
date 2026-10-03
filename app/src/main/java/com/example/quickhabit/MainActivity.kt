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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickHabitTheme {
                HomeScreen(onHabitClick = {}, onSettingsClick = {})
            }
        }
    }
}

// ---- Screens ----

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onHabitClick: (Int) -> Unit, onSettingsClick: () -> Unit) {
    var selected by remember { mutableStateOf("All") }
    val habitList = remember { mutableStateListOf<Habit>().apply { addAll(habits) } }

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
                            onCheckedChange = { isChecked ->
                                val index = habitList.indexOfFirst { it.id == habit.id }
                                habitList[index] = habit.copy(doneToday = isChecked)
                            },
                            onClick = { onHabitClick(habit.id) }
                        )
                    }
                }
            }
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
        HomeScreen(onHabitClick = {}, onSettingsClick = {})
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
        HomeScreen(onHabitClick = {}, onSettingsClick = {})
    }
}