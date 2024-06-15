package com.example.abbfinalprojectweatherapp.dependency_injection

import com.google.gson.Gson
import org.koin.dsl.module
import kotlin.math.sin

val serializerModule = module {
    single { Gson() }
}