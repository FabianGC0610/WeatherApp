package mx.kodemia.weatherapp.core

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import mx.kodemia.weatherapp.model.OneCall
import mx.kodemia.weatherapp.model.WeatherEntity

object SharedPreferencesInstance {

    //Se crea la instancia
    val sharedPref = SharedPreferencesInstance

    //Se crea la variable para obtener las preferencias
    lateinit var sharedPreferences: SharedPreferences

    //Se crea el editor
    lateinit var editor: SharedPreferences.Editor

    //Obtenemos la instancia de nuestro objeto
    fun obtenerInstancia(context: Context): SharedPreferencesInstance{
        sharedPreferences = context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        return sharedPref
    }

    //Limpiar los registros de SharedPreferences
    fun limpiarPreferencias(){
        editor.clear()
        editor.apply()
    }

    // Se guarda el elemento individual de una transaccion
    fun guardarTemperatura(weatherEntity: OneCall){
        with(editor){
            putString("temperatura", weatherEntity.current.temp.toString())
            apply()
        }
    }

    //Se guarda el token
    fun guardarToken(){
        with(editor){
            putString("token","37fb2ab875e61b9769e410901358661b")
            apply()
        }
    }

    //Se obtiene la temperatura
    fun obtenerTemp(): String?{
        return sharedPreferences.getString("temperatura", null)
    }

    //Se obtiene el token
    fun obtenerToken(): String?{
        return sharedPreferences.getString("token", null)
    }

}