package com.example.quickhabit

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quickhabit.data.Habit
import com.example.quickhabit.data.filters
import com.example.quickhabit.data.habits
import com.example.quickhabit.ui.home.HomeUiState
import com.example.quickhabit.ui.theme.QuickHabitTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quickhabit.ui.home.HomeViewModel
import android.util.Log

class MainActivity : ComponentActivity() {
    private val tag = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate")
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
    override fun onStart() {
        super.onStart()
        Log.d(tag, "onStart")
    }
    override fun onResume() {
        super.onResume()
        Log.d(tag, "onResume")
    }
    override fun onPause() {
        super.onPause()
        Log.d(tag, "onPause")
    }
    override fun onStop() {
        super.onStop()
        Log.d(tag, "onStop")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "onDestroy")
    }
}
// ---- Navigation ----
@Composable
fun QuickHabitApp(darkTheme: Boolean, onDarkThemeChange: (Boolean) -> Unit) {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = viewModel()

    NavHost(navController = navController, startDestination = "home") {
        composable ("home"){
            HomeRoute(
                viewModel = homeViewModel,
                onHabitClick = { id -> navController.navigate("detail/$id") },
                onSettingsClick = { navController.navigate("settings") }
            )
        }
        composable(
            route = "detail/{habitId}",
            arguments = listOf(navArgument("habitId") { type = NavType.IntType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getInt("habitId")
            val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
            val habit = uiState.allHabits.find { it.id == habitId }
            if (habit != null) {
                HabitDetailScreen(
                    habit = habit,
                    onToggleDone = { homeViewModel.onToggleHabit(habit.id, it) },
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

@Composable
fun HomeRoute(
    viewModel: HomeViewModel,
    onHabitClick: (Int) -> Unit,
    onSettingsClick: () -> Unit
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onFilterSelected = viewModel::onFilterSelected,
        onToggle = viewModel::onToggleHabit,
        onHabitClick = onHabitClick,
        onSettingsClick = onSettingsClick,
        onRetry = viewModel::onRetry
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onFilterSelected: (String) -> Unit,
    onToggle: (Int, Boolean) -> Unit,
    onHabitClick: (Int) -> Unit,
    onSettingsClick: () -> Unit,
    onRetry: () -> Unit
) {


    Scaffold(
        topBar = {
            TopAppBar(

                title = {
                    Text(
                        text = "QuickHabit", // или habit.name / "Settings"
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)){
            when {
                uiState.isLoading -> LoadingState(modifier = Modifier.fillMaxSize())
                uiState.error != null -> ErrorState(
                    message = uiState.error,
                    onRetry = onRetry,
                    modifier = Modifier.fillMaxSize()
                )
                else -> {
                    FilterChipRow(
                        filters = filters,
                        selected = uiState.selectedFilter,
                        doneCount = uiState.doneCount,
                        totalCount = uiState.totalCount,
                        onSelect = onFilterSelected
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if(uiState.habits.isEmpty()) {
                        EmptyState(
                            title = "No habits here",
                            subtitle = "Try another filter",
                            buttonText = "Show all",
                            onButtonClick = { onFilterSelected("All") },
                            modifier = Modifier.weight(1f)
                        )
                    }else{
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(uiState.habits, key = { it.id })  { habit ->
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HabitAvatar(
                name = habit.name,
                size = 96.dp,
                textStyle = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = habit.name,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            StreakPill(days = habit.streak)
            Spacer(modifier = Modifier.height(24.dp))
            InfoCard(
                icon = Icons.Default.Notifications,
                label = "Reminder",
                value = habit.reminderTime
            )
            Spacer(modifier = Modifier.height(8.dp))
            InfoCard(
                icon = Icons.Default.Info,
                label = "About",
                value = habit.description
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { onToggleDone(!habit.doneToday) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
            ) {
                Text(
                    text = if (habit.doneToday) "Done for today" else "Mark as done",
                    style = MaterialTheme.typography.titleMedium
                )
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
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SettingRow(
                        title = "Daily reminders",
                        checked = reminders,
                        onCheckedChange = { reminders = it }
                    )
                    HorizontalDivider()
                    SettingRow(
                        title = "Dark theme",
                        checked = darkTheme,
                        onCheckedChange = onDarkThemeChange
                    )
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
    val containerColor by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceContainerHigh,
        label = "cardColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HabitAvatar(name = habit.name)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (checked) TextDecoration.LineThrough
                    else TextDecoration.None
                )
                Spacer(modifier = Modifier.height(4.dp))
                StreakPill(days = habit.streak)
            }
            Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
fun HabitAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.firstOrNull()?.uppercase() ?: "?",
            style = textStyle,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun StreakPill(days: Int, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text = "🔥 $days days",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun FilterChipRow(
    filters: List<String>,
    selected: String,
    doneCount: Int,
    totalCount: Int,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            val isSelected = filter == selected
            val count = when (filter){
                "Done" -> doneCount
                "Not done" -> totalCount - doneCount
                else -> totalCount
            }

            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surface,
                animationSpec = tween(durationMillis = 200),
                label = "chipBg"
            )

            val textColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = tween(durationMillis = 200),
                label = "chipText"
            )

            val borderColor = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outlineVariant

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .border(
                        width = 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clickable { onSelect(filter) },
                color = backgroundColor,
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = textColor
                        )
                    }
                    Text(
                        text = "$filter ($count)",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = textColor
                    )
                }
            }
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
        Box(
            modifier = Modifier
                .size(144.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_empty_habits),
                contentDescription = "Empty list illustration",
                modifier = Modifier.size(96.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        }
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

@Composable
fun InfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = value, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyState(
        title = "Something went wrong",
        subtitle = message,
        buttonText = "Retry",
        onButtonClick = onRetry,
        modifier = modifier
    )
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
        FilterChipRow(
            filters = filters,
            selected = "All",
            doneCount = 3,
            totalCount = 10,
            onSelect = {}
        )
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

@Composable
private fun HomePreviewHost(uiState: HomeUiState) {
    QuickHabitTheme {
        HomeScreen(
            uiState = uiState,
            onFilterSelected = {},
            onToggle = { _, _ -> },
            onHabitClick = {},
            onSettingsClick = {},
            onRetry = {}
        )
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeContentPreview() {
    HomePreviewHost(
        HomeUiState(
            habits = habits,
            allHabits = habits,
            doneCount = habits.count { it.doneToday },
            totalCount = habits.size,
            isLoading = false
        )
    )
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeContentDarkPreview() {
    HomePreviewHost(
        HomeUiState(
            habits = habits,
            allHabits = habits,
            doneCount = habits.count { it.doneToday },
            totalCount = habits.size,
            isLoading = false
        )
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeLoadingPreview() {
    HomePreviewHost(HomeUiState(isLoading = true))
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeEmptyPreview() {
    HomePreviewHost(
        HomeUiState(
            habits = emptyList(),
            selectedFilter = "Done",
            totalCount = habits.size,
            isLoading = false
        )
    )
}

// Состояние Error: есть текст ошибки
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeErrorPreview() {
    HomePreviewHost(HomeUiState(isLoading = false, error = "Could not load habits"))
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