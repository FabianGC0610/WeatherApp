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
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import mx.kodemia.weatherapp.R
import mx.kodemia.weatherapp.core.Alerts
import mx.kodemia.weatherapp.core.SharedPreferencesInstance
import mx.kodemia.weatherapp.databinding.ActivityMainBinding
import mx.kodemia.weatherapp.model.*
import mx.kodemia.weatherapp.utils.checkForInternet
import mx.kodemia.weatherapp.view.formats.Formats
import mx.kodemia.weatherapp.viewmodels.MainActivityViewModel

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivityError"
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    private val format = Formats
    private val alert = Alerts
    private val location = mx.kodemia.weatherapp.core.Location
    private val shared = SharedPreferencesInstance

    var unit = "metric"
    var languageCode = "es"

    val viewModel: MainActivityViewModel by viewModels()

    private var latitude = ""
    private var longitude = ""

    private var units = false
    private var language = false

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getLastLocationAction()

        givePreferences()
    }

    private fun init() {
        //binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.onCreate()
    }

    private fun givePreferences(){
        units = shared.getPreferences(this).units
        language = shared.getPreferences(this).language
    }

    private fun sendDataWeather(
        lat: String,
        lon: String,
        units: String?,
        lang: String?,
        appid: String
    ) {
        viewModel.getWeather(lat, lon, units, lang, appid)
    }

    private fun sendDataCity(lat: String, lon: String, appid: String) {
        viewModel.getCity(lat, lon, appid)
    }

    private fun observers() {
        viewModel.errorWather.observe(this, ::errorWeather)
        viewModel.getWeatherResponse.observe(this, ::getWeather)

        viewModel.errorCity.observe(this, ::errorCity)
        viewModel.getCityResponse.observe(this, ::getCity)
    }

    private fun errorWeather(b: Boolean) {
        if (b) {
            binding.apply {
                detailsContainerFirstView.isVisible = false
                detailsContainerSecondView.isVisible = false
                errorContainer.isVisible = true
                progressBarIndicator.isVisible = false
            }
        }
    }

    private fun getWeather(oneCall: OneCall) {
        format.formatResponse(oneCall, this, binding)
    }

    private fun errorCity(b: Boolean) {
        if (b) {
            alert.showError(getString(R.string.no_city_message), this)
            binding.addressTextView.text = getString(R.string.na_city)
        }
    }

    private fun getCity(cityEntity: List<CityEntity>) {
        format.formatResponseCity(cityEntity, binding)

    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(onLocation: (location: Location) -> Unit) {
        Log.d(TAG, "Aqui estoy: $latitude Long: $longitude")
        fusedLocationClient.lastLocation
            .addOnCompleteListener { taskLocation ->
                if (taskLocation.isSuccessful && taskLocation.result != null) {
                    val location = taskLocation.result

                    latitude = location?.latitude.toString()
                    longitude = location?.longitude.toString()
                    Log.e(TAG, "GetLastLoc Lat: $latitude Long: $longitude")

                    onLocation(taskLocation.result)
                } else {
                    Log.w(TAG, getString(R.string.getLastLoc_Excep), taskLocation.exception)
                    alert.showSnackbar(R.string.no_location_detected, activity = this)
                }
            }
    }

    private fun setupViewData(location: Location) {

        if (checkForInternet(this)) {
            lifecycleScope.launch {
                latitude = location.latitude.toString()
                longitude = location.longitude.toString()
            }
        } else {
            binding.detailsContainerFirstView.isVisible = false
            binding.detailsContainerFirstView.isVisible = false
            binding.errorContainer.isVisible = false
        }
    }

    private fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            //Provide an additional rationale to the user. This would happen if the user denied the
            // request previously, but didn´t check the "Don´t ask again" checkbox.
            alert.showSnackbar(R.string.permission_retionale, android.R.string.ok, this)
            {
                //Request permission
                location.startLocationPermissionRequest(this)
            }
        } else {
            //Request permission. It´s possible this can be auto answered if device policy
            //Si la configuracion del dispositivo define el permiso a un estado prefefinido o
            //si  el usuario anteriormente activo "No preguntar de nuevo"
            location.startLocationPermissionRequest(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                //Si el flujo es interrumpido, la solicitud de permiso es cancelada y se reciben arrays vacios.
                grantResults.isEmpty() -> Log.i(TAG, getString(R.string.user_interac_canceled))

                //Permiso Otorgado
                (grantResults[0] == PackageManager.PERMISSION_GRANTED) -> getLastLocation(this::setupViewData)

                else -> {
                    alert.showSnackbar(
                        R.string.permission_denied_explanation,
                        R.string.settings,
                        activity = this
                    ) {
                        //Construye el intent que muestra la ventaa de configuracion del app
                        val intent = Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", getString(R.string.package_name), null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun notInternetAction(){
        binding.buttonRequestService.setOnClickListener {
            if(checkForInternet(this)){
                binding.progressBarIndicator.isVisible = true
                binding.buttonRequestService.isVisible = false
                sendDataWeather(latitude,
                    longitude,
                    unit,
                    languageCode,
                    getString(R.string.api_key))
                sendDataCity(latitude, longitude, getString(R.string.api_key))
            }else{
                binding.progressBarIndicator.isVisible = false
                alert.showError(getString(R.string.no_internet_yet), this)
            }
        }
    }

    private fun getLastLocationAction(){
        if (!location.checkPermissions(this)) {
            requestPermissions()
        } else {
            getLastLocation() { location ->
                if (units) {
                    unit = getString(R.string.imperial)
                } else {
                    unit = getString(R.string.metric)
                }
                if (language) {
                    languageCode = getString(R.string.english)
                } else {
                    languageCode = getString(R.string.spanish)
                }
                if (checkForInternet(this)) {
                    sendDataWeather(
                        latitude,
                        longitude,
                        unit,
                        languageCode,
                        getString(R.string.api_key)
                    )
                    sendDataCity(latitude, longitude, getString(R.string.api_key))
                } else {
                    alert.showError(getString(R.string.no_internet), this)
                    binding.buttonRequestService.isVisible = true
                    binding.progressBarIndicator.isVisible = false
                }
                observers()
            }
            notInternetAction()
        }
    }

}