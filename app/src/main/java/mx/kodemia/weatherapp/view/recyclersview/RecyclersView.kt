package mx.kodemia.weatherapp.view.recyclersview

import android.app.Activity
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mx.kodemia.weatherapp.R
import mx.kodemia.weatherapp.model.Current
import mx.kodemia.weatherapp.model.Daily
import mx.kodemia.weatherapp.model.OneCall
import mx.kodemia.weatherapp.model.RecyclerInfo
import mx.kodemia.weatherapp.view.adapters.DaysAdapter
import mx.kodemia.weatherapp.view.adapters.HoursAdapter
import mx.kodemia.weatherapp.view.adapters.InfoAdapter
import java.text.SimpleDateFormat
import java.util.*

object RecyclersView {

    private val listInfoFirstView: MutableList<RecyclerInfo> = mutableListOf()
    private val listInfoSecondView: MutableList<RecyclerInfo> = mutableListOf()
    private val listIncons: MutableList<Int> = mutableListOf()

    fun initRecycler(recyclerViewFirstView: RecyclerView, recyclerViewSecondView: RecyclerView, weatherEntity: OneCall, activity: Activity){

        listInfoFirstView.add(
            RecyclerInfo(weatherEntity.current.humidity.toString(),
                R.string.humidity)
        )
        listInfoSecondView.add(
            RecyclerInfo(weatherEntity.daily[1].humidity.toString(),
                R.string.humidity)
        )
        listIncons.add(R.drawable.humidity)

        listInfoFirstView.add(
            RecyclerInfo(weatherEntity.current.pressure.toString(),
                R.string.pressure)
        )
        listInfoSecondView.add(
            RecyclerInfo(weatherEntity.daily[1].pressure.toString(),
                R.string.pressure)
        )
        listIncons.add(R.drawable.pressure)

        listInfoFirstView.add(
            RecyclerInfo(weatherEntity.current.wind_speed.toString() + "km/h",
                R.string.wind)
        )
        listInfoSecondView.add(
            RecyclerInfo(weatherEntity.daily[1].wind_speed.toString() + "km/h",
                R.string.wind)
        )
        listIncons.add(R.drawable.wind)

        val sunrise = weatherEntity.current.sunrise
        val sunriseSecond = weatherEntity.daily[1].sunrise
        val sunriseFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
        val sunriseFormatSecond = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunriseSecond * 1000))
        listInfoFirstView.add(RecyclerInfo(sunriseFormat, R.string.sunrise))
        listInfoSecondView.add(RecyclerInfo(sunriseFormatSecond, R.string.sunrise))
        listIncons.add(R.drawable.sunrise)

        val sunset = weatherEntity.current.sunset
        val sunsetSecond = weatherEntity.daily[1].sunset
        val sunsetFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))
        val sunsetFormatSecond = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunsetSecond * 1000))
        listInfoFirstView.add(RecyclerInfo(sunsetFormat, R.string.sunset))
        listInfoSecondView.add(RecyclerInfo(sunsetFormatSecond, R.string.sunset))
        listIncons.add(R.drawable.sunset)

        val adapterFirstView = InfoAdapter(activity,listInfoFirstView,listIncons)
        val adapterSecondView = InfoAdapter(activity,listInfoSecondView,listIncons)
        recyclerViewFirstView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
            adapter = adapterFirstView
        }

        recyclerViewSecondView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
            adapter = adapterSecondView
        }
    }

    fun initRecyclerHours(hours: List<Current>, recyclerView: RecyclerView, context: Context){
        val adaptador = HoursAdapter(context,hours)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
            adapter = adaptador
        }
    }

    fun initRecyclerDays(days: List<Daily>, recyclerView: RecyclerView, context: Context){
        val adapterView = DaysAdapter(context,days)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterView
        }
    }

}