package mx.kodemia.weatherapp.viewmodels

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.kodemia.weatherapp.databinding.ActivityMainBinding
import mx.kodemia.weatherapp.model.CityEntity
import mx.kodemia.weatherapp.model.OneCall
import mx.kodemia.weatherapp.model.WeatherEntity
import mx.kodemia.weatherapp.network.service.GetCity
import mx.kodemia.weatherapp.network.service.GetWeather
import mx.kodemia.weatherapp.view.SettingsActivity
import java.io.IOException

class MainActivityViewModel: ViewModel() {

    //Service
    lateinit var serviceGetWeather: GetWeather
    lateinit var serviceGetCity: GetCity

    //LiveDatas
    val getWeatherResponse = MutableLiveData<OneCall>()
    val getCityResponse = MutableLiveData<List<CityEntity>>()
    val errorWather = MutableLiveData<Boolean>()
    val loadingWeather = MutableLiveData<Boolean>()
    val errorCity = MutableLiveData<Boolean>()
    val loadingCity = MutableLiveData<Boolean>()

    private lateinit var binding: ActivityMainBinding

    fun onCreate(){
        serviceGetWeather = GetWeather()
        serviceGetCity = GetCity()
    }

    //Funcion
    fun getWeather(lat: String, lon: String, units: String?, lang: String?, appid: String){
        viewModelScope.launch {
            loadingWeather.postValue(true)
            val response = serviceGetWeather.getWeatherService(lat, lon, units, lang, appid)
            try{
                if (response.isSuccessful){
                    getWeatherResponse.postValue(response.body())
                }else{
                    errorWather.postValue(true)
                    //binding.errorContainer.isVisible = true // Checar este detalle, (no va)
                }
                loadingWeather.postValue(false)
            }catch (io: IOException){
                errorWather.postValue(true)
                loadingWeather.postValue(false)
            }
        }
    }

    fun getCity(lat: String, lon:String, appid: String){
        viewModelScope.launch {
            loadingCity.postValue(true)
            val response = serviceGetCity.getCityService(lat, lon, appid)
            try {
                if (response.isSuccessful){
                    getCityResponse.postValue(response.body())
                }else{
                    errorCity.postValue(true)
                    //binding.errorContainer.isVisible = true
                }
                loadingCity.postValue(false)
            }catch (io: IOException){
                errorCity.postValue(true)
                loadingCity.postValue(false)
            }
        }
    }

}