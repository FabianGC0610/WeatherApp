package mx.kodemia.weatherapp.model

import java.io.Serializable

data class OneCall(
    val current: Current,
    val hourly: List<Current>,
    var city: CityEntity?
): Serializable
