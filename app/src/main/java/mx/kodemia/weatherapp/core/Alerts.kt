package mx.kodemia.weatherapp.core

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import mx.kodemia.weatherapp.databinding.ActivityMainBinding

object Alerts {

    fun showError(message: String, context: Context){
        Toast.makeText(context,message, Toast.LENGTH_LONG).show()
    }

    fun showIndicator(visible: Boolean, binding: ActivityMainBinding){
        binding.progressBarIndicator.isVisible = visible
    }

    fun showSnackbar(
        snackStrId: Int,
        actionStrId: Int = 0,
        activity: Activity,
        listener: View.OnClickListener? = null
    ){
        val snackbar = Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(snackStrId),
            BaseTransientBottomBar.LENGTH_INDEFINITE
        )

        if(actionStrId != 0 && listener != null){
            snackbar.setAction(activity.getString(actionStrId), listener)
        }
        snackbar.show()
    }

}