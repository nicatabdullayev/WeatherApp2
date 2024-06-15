package com.example.abbfinalprojectweatherapp.dependency_injection

import com.example.abbfinalprojectweatherapp.network.WeatherDataRepository
import com.example.abbfinalprojectweatherapp.network.api.WeatherApi
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit


val networkModule = module {
    factory { okHttpClient() }
    single { retrofilt(okHttpClient = get () ) }
    factory { weatherApi(retrofit = get()) }
}


private fun okHttpClient() = OkHttpClient.Builder()
    .connectTimeout(30,TimeUnit.SECONDS)
    .writeTimeout(30,TimeUnit.SECONDS)
    .readTimeout(30,TimeUnit.SECONDS)
    .retryOnConnectionFailure(false)
    .build()

private fun retrofilt(okHttpClient: OkHttpClient) = Retrofit.Builder()
    .client(okHttpClient)
    .baseUrl(WeatherApi.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

private fun weatherApi(retrofit: Retrofit) = retrofit.create(WeatherApi::class.java)