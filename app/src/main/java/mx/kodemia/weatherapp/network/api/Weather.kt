package mx.kodemia.weatherapp.network.api

import mx.kodemia.weatherapp.model.WeatherEntity
import retrofit2.http.GET
import retrofit2.http.Query

interface Weather {

    @GET("data/2.5/onecall")
    suspend fun getWeatherByLoc(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        //@Query("exclude") exclude: String,
        @Query("units") units: String?,
        @Query("lang") lang: String?, //Para el idioma
        @Query("appid") appid: String
    ): WeatherEntity

}