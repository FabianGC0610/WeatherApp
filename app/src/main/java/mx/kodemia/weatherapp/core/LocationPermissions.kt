package mx.kodemia.weatherapp.core

import android.Manifest
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import mx.kodemia.weatherapp.R

private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

fun checkPermissions(context: Context) =
    ActivityCompat.checkSelfPermission(context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PermissionChecker.PERMISSION_GRANTED

fun startLocationPermissionRequest(activity: Activity){
    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
        REQUEST_PERMISSIONS_REQUEST_CODE)
}