package mx.kodemia.weatherapp.view.adapters

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
import java.text.SimpleDateFormat
import java.util.*

class HoursAdapter(private val context: Context, private val listHours: List<Current>):RecyclerView.Adapter<HoursAdapter.HoursHolder>()  {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HoursAdapter.HoursHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cardview_hours,parent,false)
        return HoursAdapter.HoursHolder(view)
    }

    override fun onBindViewHolder(holder: HoursAdapter.HoursHolder, position: Int) {
        val hours = listHours.get(position)
        with(holder){

            val icon = hours.weather.first().icon
            val iconUrl = "https://openweathermap.org/img/w/$icon.png"
            val dateFormatter = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
            val hour = dateFormatter.format(Date(hours.dt*1000))

            tv_temp_hours.text = hours.temp.toString()
            iv_icon_hours.load(iconUrl)
            tv_time_hours.text = hour

        }
    }

    override fun getItemCount(): Int = listHours.size

    class HoursHolder(view: View): RecyclerView.ViewHolder(view){
        val iv_icon_hours: ImageView = view.findViewById(R.id.imageViewIconHours)
        val tv_time_hours: TextView = view.findViewById(R.id.textViewTimeHours)
        val tv_temp_hours: TextView = view.findViewById(R.id.textViewTempHours)
    }

}