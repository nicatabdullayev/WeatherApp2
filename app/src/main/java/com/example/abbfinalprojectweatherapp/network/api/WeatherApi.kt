package com.example.abbfinalprojectweatherapp.network.api

import com.example.abbfinalprojectweatherapp.data.RemoteLocation
import com.example.abbfinalprojectweatherapp.data.RemoteWeatherData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {



    companion object{
        const val BASE_URL = "https://api.weatherapi.com/v1/"
        const val API_KEY ="12bd7534ea8348b59d9220156241306"
    }

    @GET("search.json")
    suspend fun searchLocation(
        @Query("key") key : String = API_KEY,
        @Query("q") query: String
    ):Response<List<RemoteLocation>>

    @GET("forecast.json")
    suspend fun getWeatherData(
        @Query("key") key: String = API_KEY,
        @Query("q") query: String
    ):Response<RemoteWeatherData>
}