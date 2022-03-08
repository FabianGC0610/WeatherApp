package mx.kodemia.weatherapp.network.service

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mx.kodemia.weatherapp.model.WeatherEntity
import mx.kodemia.weatherapp.network.api.RetrofitInstance
import mx.kodemia.weatherapp.network.api.Weather
import retrofit2.Response

class GetWeather(context: Context) {

    //Se instancia el servicio de retrofit con la peticion de LogIn
    private val retrofit = RetrofitInstance.RetrofitInstance.getRetrofit(context).create(Weather::class.java)

    //Se crea la funcion para mandar la peticion con los parametros necesarios para realizarla
    //Con un tipo de retorno del modelo de la respuesta
    suspend fun getWeatherService(lat: String, lon: String, units: String?, lang: String?, appid: String): Response<WeatherEntity> {
        return withContext(Dispatchers.IO){
            val response = retrofit.getWeatherByLoc(lat, lon, units, lang, appid)
            response
        }
    }

}