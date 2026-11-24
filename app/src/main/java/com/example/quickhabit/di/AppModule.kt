package com.example.quickhabit.di

import com.example.quickhabit.data.repository.HabitRepositoryImpl
import com.example.quickhabit.domain.FilterHabitsUseCase
import com.example.quickhabit.domain.GetHabitsUseCase
import com.example.quickhabit.domain.ToggleHabitUseCase
import com.example.quickhabit.domain.repository.HabitRepository
import org.koin.dsl.module
import com.example.quickhabit.ui.home.HomeViewModel
import org.koin.core.module.dsl.viewModel
import com.example.quickhabit.notification.NotificationHelper
import com.example.quickhabit.ui.timer.TimerViewModel
import org.koin.android.ext.koin.androidContext
import com.example.quickhabit.data.repository.AuthRepositoryImpl
import com.example.quickhabit.domain.repository.AuthRepository
import com.example.quickhabit.ui.auth.LoginViewModel

val appModule = module {
    single<HabitRepository> { HabitRepositoryImpl(get()) }
    factory { GetHabitsUseCase(get()) }
    factory { ToggleHabitUseCase(get()) }
    factory { FilterHabitsUseCase() }
    viewModel { HomeViewModel(get(), get(), get()) }
    single { NotificationHelper(androidContext()) }
    viewModel { TimerViewModel(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    viewModel { LoginViewModel(get()) }
}