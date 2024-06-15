package com.example.abbfinalprojectweatherapp.dependency_injection

import com.example.abbfinalprojectweatherapp.network.WeatherDataRepository
import org.koin.dsl.module


val repositoryModule = module {
    single { WeatherDataRepository(weatherApi = get()) }
}