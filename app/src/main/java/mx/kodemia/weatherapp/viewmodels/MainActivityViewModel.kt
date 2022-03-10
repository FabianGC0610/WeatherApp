package mx.kodemia.weatherapp.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.kodemia.weatherapp.model.OneCall
import mx.kodemia.weatherapp.model.WeatherEntity
import mx.kodemia.weatherapp.network.service.GetWeather

class MainActivityViewModel: ViewModel() {

    //Service
    lateinit var serviceGetWeather: GetWeather

    //LiveDatas
    val getWeatherResponse = MutableLiveData<OneCall>()

    fun onCreate(){
        serviceGetWeather = GetWeather()
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

}