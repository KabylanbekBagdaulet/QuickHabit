package com.example.quickhabit

import android.app.Application
import com.example.quickhabit.di.appModule
import com.example.quickhabit.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import com.example.quickhabit.di.firebaseModule

class QuickHabitApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@QuickHabitApplication)
            modules(networkModule, firebaseModule, appModule)
        }
    }
}