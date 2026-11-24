package com.example.quickhabit.ui.timer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private const val TIMER_SECONDS = 10

@Composable
fun HabitTimerSection(habitName: String, viewModel: TimerViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var notificationsDenied by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        notificationsDenied = !granted
        viewModel.start(habitName, TIMER_SECONDS)
    }

    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text("Focus timer", style = MaterialTheme.typography.titleMedium)

        when (val s = state) {
            is TimerUiState.Idle -> Button(modifier = Modifier.fillMaxWidth(), onClick = {
                val needsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
                        PackageManager.PERMISSION_GRANTED
                if (needsPermission) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    notificationsDenied = false
                    viewModel.start(habitName, TIMER_SECONDS)
                }
            }) { Text("Start $TIMER_SECONDS s") }

            is TimerUiState.Running -> {
                Text("${s.secondsLeft} s left")
                OutlinedButton(onClick = viewModel::cancel) { Text("Cancel") }
            }

            is TimerUiState.Finished -> {
                Text("Time is up!")
                Button(onClick = viewModel::cancel) { Text("OK") }
            }
        }

        if (notificationsDenied) {
            Text(
                "Notifications are off. You will see the timer only on screen.",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}