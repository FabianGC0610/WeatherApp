package mx.kodemia.weatherapp.network.api

import mx.kodemia.weatherapp.model.CityEntity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface City {
    @GET("geo/1.0/reverse")
    suspend fun getCitiesByLatLng(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String
    ): Response<List<CityEntity>>
}