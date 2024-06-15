package com.example.abbfinalprojectweatherapp.location

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abbfinalprojectweatherapp.data.CurrentLocation
import com.example.abbfinalprojectweatherapp.data.RemoteLocation
import com.example.abbfinalprojectweatherapp.home.HomeViewModel
import com.example.abbfinalprojectweatherapp.network.WeatherDataRepository
import kotlinx.coroutines.launch
import retrofit2.http.Query

class LocationViewModel (private val weatherDataRepository: WeatherDataRepository): ViewModel()  {
    private val _seacrhResult = MutableLiveData<SearchResultDataState>()
    val seacrhResult: LiveData<SearchResultDataState> get() = _seacrhResult


    fun searchLocation(query: String){
        viewModelScope.launch {
            emitSearchResultUiState(isLoading = true)
            val searchResult = weatherDataRepository.searchLocation(query)
            if (searchResult.isNullOrEmpty()){
                emitSearchResultUiState(error = "Location Not found , please try again")
            }else{
                emitSearchResultUiState(locations = searchResult)
            }
        }
    }

    private fun emitSearchResultUiState(isLoading: Boolean = false,
                                        locations: List<RemoteLocation>? = null,
                                        error:String? = null) {
        val searchResultDataState = SearchResultDataState(isLoading, locations, error)
        _seacrhResult.value = searchResultDataState


    }




    data class SearchResultDataState(
        val isLoading: Boolean,
        val locations: List<RemoteLocation>?,
        val error:String?
    )
}