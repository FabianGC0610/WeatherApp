package mx.kodemia.weatherapp.view

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.preference.PreferenceManager
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
import mx.kodemia.weatherapp.model.*
import mx.kodemia.weatherapp.utils.checkForInternet
import mx.kodemia.weatherapp.view.adapters.DaysAdapter
import mx.kodemia.weatherapp.view.adapters.HoursAdapter
import mx.kodemia.weatherapp.view.adapters.InfoAdapter
import mx.kodemia.weatherapp.view.formats.Formats
import mx.kodemia.weatherapp.view.recyclersview.RecyclersView
import mx.kodemia.weatherapp.viewmodels.MainActivityViewModel
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivityError"
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    private val format = Formats

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
                    if(units){
                        unit = "imperial"
                    }else{
                        unit = "metric"
                    }
                    if(language){
                        languageCode = "en"
                    }else{
                        languageCode = "es"
                    }
                    mandarDatosWeather(latitude,longitude,unit,languageCode,"37fb2ab875e61b9769e410901358661b")
                    mandarDatosCity(latitude,longitude,getString(R.string.api_key))
                    observers()
                }
            }

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        units = sharedPreferences.getBoolean("unitsApp", false)
        language = sharedPreferences.getBoolean("languageApp", false)
    }

    private fun IntentSettings() {
        startActivity(Intent(this,SettingsActivity::class.java))
        finish()
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

    private fun mandarDatosCity(lat: String, lon: String, appid: String){
        viewModel.getCity(lat, lon, appid)
    }

     private fun observers(){
         viewModel.errorWather.observe(this,::errorWeather)
         viewModel.loadingWeather.observe(this,::loadingWeather)
         viewModel.getWeatherResponse.observe(this,::getWeather)

         viewModel.errorCity.observe(this,::errorCity)
         viewModel.loadingCity.observe(this,::loadingCity)
         viewModel.getCityResponse.observe(this,::getCity)
     }

    private fun loadingWeather(b: Boolean){

    }

    private fun errorWeather(b: Boolean){
        if(b){
            binding.apply {
                detailsContainerFirstView.isVisible = false
                detailsContainerSecondView.isVisible = false
                errorContainer.isVisible = true
            }
        }
    }

    private fun getWeather(oneCall: OneCall){
        format.formatResponse(oneCall,this,binding)
    }

    private fun loadingCity(b: Boolean){

    }

    private fun errorCity(b: Boolean){
        if(b){
            //toast para avisar que no la encontro
            binding.addressTextView.text = getString(R.string.na_city)
        }
    }

    private fun getCity(cityEntity: List<CityEntity>){
        format.formatResponseCity(cityEntity,binding)

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
                binding.detailsContainerFirstView.isVisible = false
                binding.detailsContainerFirstView.isVisible = false
                binding.errorContainer.isVisible = false
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

    private fun showError(message: String){
        Toast.makeText(this,message, Toast.LENGTH_LONG).show()
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

}