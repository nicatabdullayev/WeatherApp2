package com.example.abbfinalprojectweatherapp.dependency_injection

import com.example.abbfinalprojectweatherapp.home.HomeViewModel
import com.example.abbfinalprojectweatherapp.location.LocationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(weatherDataRepository = get()) }
    viewModel { LocationViewModel(weatherDataRepository = get()) }

}