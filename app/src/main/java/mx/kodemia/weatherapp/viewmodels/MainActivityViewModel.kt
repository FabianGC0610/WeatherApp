package mx.kodemia.weatherapp.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.kodemia.weatherapp.model.CityEntity
import mx.kodemia.weatherapp.model.OneCall
import mx.kodemia.weatherapp.model.WeatherEntity
import mx.kodemia.weatherapp.network.service.GetCity
import mx.kodemia.weatherapp.network.service.GetWeather

class MainActivityViewModel: ViewModel() {

    //Service
    lateinit var serviceGetWeather: GetWeather
    lateinit var serviceGetCity: GetCity

    //LiveDatas
    val getWeatherResponse = MutableLiveData<OneCall>()
    val getCityResponse = MutableLiveData<List<CityEntity>>()

    fun onCreate(){
        serviceGetWeather = GetWeather()
        serviceGetCity = GetCity()
    }

    //Funcion
    fun getWeather(lat: String, lon: String, units: String?, lang: String?, appid: String){
        viewModelScope.launch {
            val response = serviceGetWeather.getWeatherService(lat, lon, units, lang, appid)
            if (response.isSuccessful){
                getWeatherResponse.postValue(response.body())
            }else {
                    Log.e("WEATHERSERROR",response.code().toString())
            }
        }
    }

    fun getCity(lat: String, lon:String, appid: String){
        viewModelScope.launch {
            val response = serviceGetCity.getCityService(lat, lon, appid)
            if (response.isSuccessful){
                getCityResponse.postValue(response.body())
            }else{
                Log.e("CITYERROR",response.code().toString())
            }
        }
    }

}