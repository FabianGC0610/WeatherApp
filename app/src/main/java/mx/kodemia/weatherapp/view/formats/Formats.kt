package mx.kodemia.weatherapp.view.formats

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import coil.load
import mx.kodemia.weatherapp.databinding.ActivityMainBinding
import mx.kodemia.weatherapp.model.CityEntity
import mx.kodemia.weatherapp.model.OneCall
import mx.kodemia.weatherapp.view.SettingsActivity
import mx.kodemia.weatherapp.view.recyclersview.RecyclersView
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

object Formats {

    private var units = false
    private val recycler = RecyclersView

    fun formatResponseCity(cityEntity: List<CityEntity>, binding: ActivityMainBinding){
        val cityName = cityEntity[0].name
        val country = cityEntity[0].country
        val address = "$cityName, $country"

        binding.apply {
            addressTextView.text = address
        }
    }

    fun formatResponse(weatherEntity: OneCall,activity: Activity, binding: ActivityMainBinding){

        var unitSymbol = "°C"

        if(units){
            unitSymbol = "°F"
        }

        try {
            val temp = "${weatherEntity.current.temp.toInt()}"
            var status = ""
            val weatherDescription = weatherEntity.current.weather[0].description
            if(weatherDescription.isNotEmpty()){
                status = (weatherDescription[0].uppercaseChar() + weatherDescription.substring(1))
            }
            val dt = weatherEntity.current.dt
            val updateAt = SimpleDateFormat(
                "EEEE, d MMMM",
                Locale.ENGLISH
            ).format(Date(dt * 1000))
            val icon = weatherEntity.current.weather[0].icon.replace('n','d')
            val iconUrl = activity.resources.getIdentifier("ic_weather_$icon", "drawable", activity.packageName)

            val iconSecond = weatherEntity.daily[1].weather.first().icon.replace('n','d')
            val iconUrlSecond = activity.resources.getIdentifier("ic_weather_$iconSecond","drawable", activity.packageName)
            val tempInDayTom = weatherEntity.daily[1].temp.day.toInt().toString()
            val tempInNightTom = "/" + weatherEntity.daily[1].temp.night.toInt().toString() + unitSymbol
            var statusTom = ""
            val forecastTom = weatherEntity.daily[1].weather.first().description
            if(forecastTom.isNotEmpty()){
                statusTom = (forecastTom[0].uppercaseChar() + forecastTom.substring(1))
            }

            binding.apply {
                dateTextView.text = updateAt
                temperatureTextView.text = temp
                textViewTempSymbol.text = unitSymbol
                statusTextView.text = status

                buttonShowDays.setOnClickListener {
                    detailsContainerFirstView.isVisible = false
                    detailsContainerSecondView.isVisible = true
                }
                buttonMinimizaCardView.setOnClickListener {
                    detailsContainerFirstView.isVisible = false
                    detailsContainerSecondView.isVisible = true
                }
                buttonSettingsFirstView.setOnClickListener {
                    IntentSettings(activity)
                }

                textViewTempInDayTom.text = tempInDayTom
                textViewTempInNightTom.text = tempInNightTom
                textViewForecastTom.text = statusTom
                iconImageViewSecondView.load(iconUrlSecond)
                buttonExpandCardView.setOnClickListener {
                    detailsContainerFirstView.isVisible = true
                    detailsContainerSecondView.isVisible = false
                }
                buttonShowHours.setOnClickListener {
                    detailsContainerFirstView.isVisible = true
                    detailsContainerSecondView.isVisible = false
                }
                buttonSettingsSecondView.setOnClickListener {
                    IntentSettings(activity)
                }

                detailsContainerFirstView.isVisible = true
                detailsContainerSecondView.isVisible = false
                iconImageView.load(iconUrl)

                recycler.initRecycler(recyclerViewInfoHome,recyclerViewInfoHomeSecondView,weatherEntity,activity)
                recycler.initRecyclerHours(weatherEntity.hourly,recyclerViewHours,activity)
                recycler.initRecyclerDays(weatherEntity.daily,recyclerViewDays,activity)
            }

            showIndicator(false,binding)
        }catch (exception: Exception){
            showError("Ha ocurrido un error con los datos", activity)
            Log.e("Error format", "Ha ocurrido un error")
            showIndicator(false,binding)
        }
    }

    private fun IntentSettings(activity: Activity) {
        activity.startActivity(Intent(activity, SettingsActivity::class.java))
        activity.finish()
    }

    private fun showIndicator(visible: Boolean, binding: ActivityMainBinding){
        binding.progressBarIndicator.isVisible = visible
    }

    private fun showError(message: String, context: Context){
        Toast.makeText(context,message, Toast.LENGTH_LONG).show()
    }

}