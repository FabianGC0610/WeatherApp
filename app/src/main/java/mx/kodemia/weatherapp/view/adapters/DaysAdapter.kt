package mx.kodemia.weatherapp.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.card.MaterialCardView
import mx.kodemia.weatherapp.R
import mx.kodemia.weatherapp.model.Current
import mx.kodemia.weatherapp.model.Daily
import java.text.SimpleDateFormat
import java.util.*

class DaysAdapter(private val context: Context, private val listDays: List<Daily>): RecyclerView.Adapter<DaysAdapter.DaysHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DaysAdapter.DaysHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cardview_days,parent,false)
        return DaysAdapter.DaysHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DaysAdapter.DaysHolder, position: Int) {
        val days = listDays.get(position + 1)
        with(holder){

            val icon = days.weather.first().icon
            val iconUrl = "https://openweathermap.org/img/w/$icon.png"
            val dateFormatter = SimpleDateFormat("EEE", java.util.Locale.ENGLISH)
            val day = dateFormatter.format(Date(days.dt*1000))

            tv_current_day.text = day
            tv_forecast_days.text = days.weather.first().main
            if(days.temp.day < 0){
                tv_temp_in_day.text = "-" + days.temp.day.toInt().toString() + "°"
            }else{
                tv_temp_in_day.text = "+" + days.temp.day.toInt().toString() + "°"
            }
            if (days.temp.night < 0){
                tv_temp_in_night.text = "-" + days.temp.night.toInt().toString() + "°"
            }else{
                tv_temp_in_night.text = "+" + days.temp.night.toInt().toString() + "°"
            }
            iv_icon_days.load(iconUrl)

        }
    }

    override fun getItemCount(): Int = listDays.size - 1

    class DaysHolder(view: View): RecyclerView.ViewHolder(view){
        val iv_icon_days: ImageView = view.findViewById(R.id.imageViewIconDays)
        val tv_temp_in_day: TextView = view.findViewById(R.id.textViewTempInDay)
        val tv_temp_in_night: TextView = view.findViewById(R.id.textViewTempInNight)
        val tv_current_day: TextView = view.findViewById(R.id.textViewDay)
        val tv_forecast_days: TextView = view.findViewById(R.id.textViewForecastDays)
    }

}