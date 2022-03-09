package mx.kodemia.weatherapp.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import mx.kodemia.weatherapp.R
import mx.kodemia.weatherapp.core.SharedPreferencesInstance
import mx.kodemia.weatherapp.core.checkPermissions
import mx.kodemia.weatherapp.core.startLocationPermissionRequest
import mx.kodemia.weatherapp.databinding.ActivityMainBinding
import mx.kodemia.weatherapp.model.Current
import mx.kodemia.weatherapp.model.OneCall
import mx.kodemia.weatherapp.model.RecyclerInfo
import mx.kodemia.weatherapp.model.WeatherEntity
import mx.kodemia.weatherapp.network.service.GetWeather
import mx.kodemia.weatherapp.utils.checkForInternet
import mx.kodemia.weatherapp.view.adapters.HoursAdapter
import mx.kodemia.weatherapp.view.adapters.InfoAdapter
import mx.kodemia.weatherapp.viewmodels.MainActivityViewModel
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivityError"
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    private val listInfo: MutableList<RecyclerInfo> = mutableListOf()
    private val listIncons: MutableList<Int> = mutableListOf()

    var unit = "metric"
    var languageCode = "es"

    val viewModel: MainActivityViewModel by viewModels()

    private var latitude = ""
    private var longitude = ""

    private var units = false
    private var language = false

    private lateinit var binding: ActivityMainBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    lateinit var shared : SharedPreferencesInstance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if(!checkPermissions(this)){
            requestPermissions()
        }else{
            getLastLocation(){ location ->
                mandarDatosWeather(latitude,longitude,unit,languageCode,"37fb2ab875e61b9769e410901358661b")
                observers(this,location,binding.recyclerViewInfoHome)
            }
        }
    }

    private fun init(){
        //Shared
        shared = SharedPreferencesInstance.obtenerInstancia(this)

        //binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.onCreate()
    }

    private fun mandarDatosWeather(lat: String, lon: String, units: String?, lang: String?, appid: String) {
        viewModel.getWeather(lat, lon, units, lang, appid)
    }
     private fun observers(context: Context, location: Location, recyclerView: RecyclerView){

         viewModel.getWeatherResponse.observe(this) {weatherEntity: OneCall ->
             shared.guardarTemperatura(weatherEntity)
             shared.guardarToken()
             lifecycleScope.launch {
                 weatherEntity.apply {
                     //Log.e("Temp por hora", this.current.hourly[1].temp.toString())
                     if(checkForInternet(context)) {
                         latitude = location.latitude.toString()
                         longitude = location.longitude.toString()
                         formatResponse(weatherEntity)
                     }else{
                         showError("Sin acceso a Internet")
                         binding.detailsContainer.isVisible = false
                     }

                 }
             }

         }

     }

    private fun requestPermissions(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )){
            //Provide an additional rationale to the user. This would happen if the user denied the
            // request previously, but didn´t check the "Don´t ask again" checkbox.
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            showSnackbar(R.string.permission_retionale, android.R.string.ok)
            {
                //Request permission
                startLocationPermissionRequest(this)
            }
        }else{
            //Request permission. It´s possible this can be auto answered if device policy
            //Si la configuracion del dispositivo define el permiso a un estado prefefinido o
            //si  el usuario anteriormente activo "No preguntar de nuevo"
            Log.i(TAG, "Solicitando permiso")
            startLocationPermissionRequest(this)
        }
    }

    private fun showSnackbar(
        snackStrId: Int,
        actionStrId: Int = 0,
        listener: View.OnClickListener? = null
    ){
        val snackbar = Snackbar.make(findViewById(android.R.id.content), getString(snackStrId),
            BaseTransientBottomBar.LENGTH_INDEFINITE
        )

        if(actionStrId != 0 && listener != null){
            snackbar.setAction(getString(actionStrId), listener)
        }
        snackbar.show()
    }

    @SuppressLint( "MissingPermission")
    private fun getLastLocation(onLocation: (location: Location) -> Unit){
        Log.d(TAG, "Aqui estoy: $latitude Long: $longitude")
        fusedLocationClient.lastLocation
            .addOnCompleteListener { taskLocation ->
                if(taskLocation.isSuccessful && taskLocation.result != null){
                    val location = taskLocation.result

                    latitude = location?.latitude.toString()
                    longitude = location?.longitude.toString()
                    Log.e(TAG, "GetLastLoc Lat: $latitude Long: $longitude")

                    onLocation(taskLocation.result)
                }else{
                    Log.w(TAG, "getLastLocation:exception", taskLocation.exception)
                    showSnackbar(R.string.no_location_detected)
                }
            }
    }

    private fun setupViewData(location: Location){
        if(checkForInternet(this)) {
            lifecycleScope.launch {
                latitude = location.latitude.toString()
                longitude = location.longitude.toString()
            }
        }else{
            showError("Sin acceso a Internet")
            binding.detailsContainer.isVisible = false
        }
    }

    private fun initRecycler(recyclerView: RecyclerView, weatherEntity: OneCall){

        listInfo.add(RecyclerInfo(weatherEntity.current.humidity.toString(),R.string.humidity))
        listIncons.add(R.drawable.humidity)

        listInfo.add(RecyclerInfo(weatherEntity.current.pressure.toString(),R.string.pressure))
        listIncons.add(R.drawable.pressure)

        listInfo.add(RecyclerInfo(weatherEntity.current.wind_speed.toString(),R.string.wind))
        listIncons.add(R.drawable.wind)

        listInfo.add(RecyclerInfo(weatherEntity.current.sunrise.toString(),R.string.sunrise))
        listIncons.add(R.drawable.sunrise)

        listInfo.add(RecyclerInfo(weatherEntity.current.sunset.toString(),R.string.sunset))
        listIncons.add(R.drawable.sunset)

        val adaptador = InfoAdapter(this,listInfo,listIncons)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
            adapter = adaptador
        }
    }

    private fun initRecyclerHours(hours: List<Current>, recyclerView: RecyclerView){
        val adaptador = HoursAdapter(this,hours)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
            adapter = adaptador
        }
    }

    private fun formatResponse(weatherEntity: OneCall){

        var unitSymbol = "°C"

        if(units){
            unitSymbol = "°F"
        }

        try {
            val temp = "${weatherEntity.current.temp.toInt()}"
            val cityName = ""//weatherEntity.name
            val country = ""//weatherEntity.sys.country
            val address = "$cityName, $country"
            //val dateNow = Calendar.getInstance().time
            val tempMin = ""//"Min: ${weatherEntity.main.temp_min.toInt()}°"
            val tempMax = ""//"Max: ${weatherEntity.main.temp_max.toInt()}°"
            var status = ""
            val weatherDescription = weatherEntity.current.weather[0].description
            if(weatherDescription.isNotEmpty()){
                status = (weatherDescription[0].uppercaseChar() + weatherDescription.substring(1))
            }
            val dt = weatherEntity.current.dt
            val updateAt = "Actualizado: ${
                SimpleDateFormat(
                    "hh:mm a",
                    Locale.ENGLISH
                ).format(Date(dt * 1000))
            }"
            val sunrise = weatherEntity.current.sunrise
            val sunriseFormat =
                SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
            val sunset = weatherEntity.current.sunset
            val sunsetFormat =
                SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))
            val wind = "${weatherEntity.current.wind_speed} km/h"
            val pressure = "${weatherEntity.current.pressure} mb"
            val humidity = "${weatherEntity.current.humidity}%"
            val feelsLike =
                getString(R.string.textSensacion) + " ${weatherEntity.current.feels_like.toInt()}$unitSymbol"
            val icon = weatherEntity.current.weather[0].icon
            val iconUrl = "https://openweathermap.org/img/w/$icon.png"

            //binding.addressTextView.text = address

            binding.apply {
                addressTextView.text = address
                dateTextView.text = updateAt
                temperatureTextView.text = temp
                textViewTempSymbol.text = unitSymbol
                statusTextView.text = status
                //tempMinTextView.text = tempMin
                //tempMaxTextViewMine.text = tempMax
                //sunriseTextViewMine.text = sunriseFormat
                //sunsetTextViewMine.text = sunsetFormat
                //windTextViewMine.text = wind
                //pressureTextViewMine.text = pressure
                //humidityTextViewMine.text = humidity
                detailsContainer.isVisible = true
                //feelsLiketextView.text = feelsLike
                iconImageView.load(iconUrl)
                initRecycler(recyclerViewInfoHome, weatherEntity)
                initRecyclerHours(weatherEntity.hourly,recyclerViewHours)
            }

            showIndicator(false)
        }catch (exception: Exception){
            showError("Ha ocurrido un error con los datos")
            Log.e("Error format", "Ha ocurrido un error")
            showIndicator(false)
        }
    }

    private fun showError(message: String){
        Toast.makeText(this,message, Toast.LENGTH_LONG).show()
    }

    private fun showIndicator(visible: Boolean){
        binding.progressBarIndicator.isVisible = visible
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, // TODO: Tenia un out antes del String
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i(TAG, "onRequestPermissionsResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                //Si el flujo es interrumpido, la solicitud de permiso es cancelada y se reciben arrays vacios.
                grantResults.isEmpty() -> Log.i(TAG, "La interaccion del usuario fue cancelada")

                //Permiso Otorgado
                (grantResults[0] == PackageManager.PERMISSION_GRANTED) -> getLastLocation(this::setupViewData)

                else -> {
                    showSnackbar(R.string.permission_denied_explanation, R.string.settings) {
                        //Construye el intent que muestra la ventaa de configuracion del app
                        val intent = Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", "mx.kodemia.climadefabiruchis", null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                    }
                }
            }
        }
    }

}