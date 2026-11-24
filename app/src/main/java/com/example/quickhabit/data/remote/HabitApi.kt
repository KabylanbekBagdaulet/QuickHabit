package com.example.quickhabit.data.remote

import retrofit2.http.GET
import retrofit2.http.Query


interface HabitApi {


    @GET("todos")
    suspend fun getTodos(@Query("_limit") limit: Int = 10): List<HabitDto>
}