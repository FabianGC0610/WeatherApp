package mx.kodemia.weatherapp.model

data class Daily(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp : Temp,
    val feels_like: Temp,
    val pressure: Int,
    val humidity: Int,
    val wind_speed: Double,
)
