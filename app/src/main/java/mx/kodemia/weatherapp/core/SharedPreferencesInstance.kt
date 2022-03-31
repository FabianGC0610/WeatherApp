package mx.kodemia.weatherapp.core

import android.content.Context
import androidx.preference.PreferenceManager
import mx.kodemia.weatherapp.model.LanguageUnits

object SharedPreferencesInstance {

    fun getPreferences(context: Context): LanguageUnits{
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return LanguageUnits(
            sharedPreferences.getBoolean("unitsApp", false),
            sharedPreferences.getBoolean("languageApp", false)
        )
    }

}