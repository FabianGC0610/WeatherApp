package mx.kodemia.weatherapp.view.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import mx.kodemia.weatherapp.R
import mx.kodemia.weatherapp.core.SharedPreferencesInstance
import mx.kodemia.weatherapp.model.RecyclerInfo

class InfoAdapter(val activity: Activity, val weatherEntity: List<RecyclerInfo>, val icon: List<Int>): RecyclerView.Adapter<InfoAdapter.InfoHolder>() {

    //SharedPreferences
    lateinit var shared : SharedPreferencesInstance

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InfoAdapter.InfoHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cardview_info,parent,false)
        return InfoHolder(view)
    }

    override fun onBindViewHolder(holder: InfoAdapter.InfoHolder, position: Int) {
        val weatherEntity = weatherEntity.get(position)
        with(holder){

            iv_Icon.setImageResource(icon[position])
            tv_info_info.text = weatherEntity.info
            tv_concept_info.text = activity.getString(weatherEntity.concept)

        }
    }

    override fun getItemCount(): Int = 5

    class InfoHolder(view: View): RecyclerView.ViewHolder(view){
        val cardView: MaterialCardView = view.findViewById(R.id.cardView_item_info)
        val iv_Icon: ImageView = view.findViewById(R.id.imageViewIconInfo)
        val tv_info_info: TextView = view.findViewById(R.id.textViewInfoInfo)
        val tv_concept_info: TextView = view.findViewById(R.id.textViewConceptInfo)
    }

}