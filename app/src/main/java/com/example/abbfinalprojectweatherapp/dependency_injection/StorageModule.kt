package com.example.abbfinalprojectweatherapp.dependency_injection

import com.example.abbfinalprojectweatherapp.storage.SharedPreferencesManager
import org.koin.dsl.module

val storageModule = module {
    single { SharedPreferencesManager(context = get(), gson = get()) }
}