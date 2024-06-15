package com.example.abbfinalprojectweatherapp.home

import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abbfinalprojectweatherapp.data.CurrentLocation
import com.example.abbfinalprojectweatherapp.data.CurrentWeather
import com.example.abbfinalprojectweatherapp.data.Forecast
import com.example.abbfinalprojectweatherapp.data.LiveDataEvent
import com.example.abbfinalprojectweatherapp.data.WeatherData
import com.example.abbfinalprojectweatherapp.network.WeatherDataRepository
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Locale

class HomeViewModel(private val weatherDataRepository: WeatherDataRepository):ViewModel() {

    private val _currentLocation = MutableLiveData<LiveDataEvent<CurrentLocationDataState>>()
    val currentLocation:LiveData<LiveDataEvent<CurrentLocationDataState>> get() = _currentLocation

    fun getCurrentLocation(
        fusedLocationProviderClient: FusedLocationProviderClient,
        geocoder: Geocoder
    ){
        viewModelScope.launch {
            emitCurrentLocationUiState(isLoading = true)
            weatherDataRepository.getCurrentLocation(
                fusedLocationProviderClient = fusedLocationProviderClient,
                onSuccess = {currentLocation ->
                    updateAddressText(currentLocation,geocoder)
                },
                onFailure = {
                    emitCurrentLocationUiState(error="Unable to fetch current Location")
                }
            )
        }
    }

    private fun updateAddressText(currentLocation: CurrentLocation,geocoder: Geocoder){
        viewModelScope.launch {
            runCatching {
                weatherDataRepository.updateAdressText(currentLocation,geocoder)
            }.onSuccess { location->
                emitCurrentLocationUiState(currentLocation = location)

            }.onFailure {
                emitCurrentLocationUiState(
                    currentLocation = currentLocation.copy(
                        location = "N/A"
                    )
                )
            }
        }

    }
    private fun emitCurrentLocationUiState(
        isLoading: Boolean = false,
        currentLocation: CurrentLocation?=null,
        error:String? = null
    ){
        val currentLocationDataState = CurrentLocationDataState(isLoading,currentLocation,error)
        _currentLocation.value = LiveDataEvent(currentLocationDataState)
    }

    data class CurrentLocationDataState(
        val isLoading: Boolean,
        val currentLocation: CurrentLocation?,
        val error:String?
    )

    //------------––––––––--------------------------------------------------------------––––––––––––––––––––-

    private val _weatherData = MutableLiveData<LiveDataEvent<WeatherDataState>>()
    val weatherData:LiveData<LiveDataEvent<WeatherDataState>> get() = _weatherData

    fun getWeatherData(latitude:Double,longitude:Double){
        viewModelScope.launch {
            emitWeatherDataUiState(isLoading = true)
            weatherDataRepository.getWeatherData(latitude,longitude)?.let { weatherData->
                emitWeatherDataUiState(
                    currentWeather = CurrentWeather(
                        icon = weatherData.current.condition.icon,
                        temperature = weatherData.current.temperature,
                        wind = weatherData.current.wind,
                        humidity = weatherData.current.humidity,
                        chanceOfRain = weatherData.forecast.forecastDay.first().day.chanceOfRain
                    ), forecast = weatherData.forecast.forecastDay.first().hourRemote.map {
                        Forecast(
                            time = getForecastTime(it.time),
                            temperature = it.temperature,
                            feelsLikeTemperature = it.feelsLikeTemperature,
                            icon = it.condition.icon
                        )
                    }
                )
            }?:emitWeatherDataUiState(error = "Unable to fetch weather data")
        }

    }

    private fun emitWeatherDataUiState(
        isLoading: Boolean = false,
        currentWeather: CurrentWeather? = null,
        forecast: List<Forecast>?=null,
        error: String?=null
    ){
        val weatherDataState = WeatherDataState(isLoading,currentWeather,forecast,error)
        _weatherData.value = LiveDataEvent(weatherDataState)
    }


data class WeatherDataState(
    val isLoading: Boolean,
    val currentWeather: CurrentWeather?,
    val forecast: List<Forecast>?,
    val error: String?
)
    private fun getForecastTime(dateTime: String):String{
        val pattern = SimpleDateFormat("yyyy-MMM-dd HH:mm",Locale.getDefault())
        val date = pattern.parse(dateTime)?:return dateTime
        return SimpleDateFormat("HH:mm",Locale.getDefault()).format(date)
    }
}