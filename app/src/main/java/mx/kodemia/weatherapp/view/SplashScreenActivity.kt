package mx.kodemia.weatherapp.view

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import mx.kodemia.weatherapp.R
import mx.kodemia.weatherapp.databinding.ActivitySplashBinding

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreenBinding: ActivitySplashBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashScreenBinding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        val splashAnimationText = AnimationUtils.loadAnimation(this@SplashScreenActivity,
            R.anim.splash_text
        )
        splashScreenBinding.apptextView.animation = splashAnimationText

        splashAnimationText.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                splashScreenBinding.apptextView.visibility = View.VISIBLE

                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 1000)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        val splashAnimationLogo = AnimationUtils.loadAnimation(this@SplashScreenActivity,
            R.anim.splash_logo
        )
        splashScreenBinding.appImageView.animation = splashAnimationLogo

        splashAnimationLogo.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                splashScreenBinding.appImageView.visibility = View.VISIBLE

                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 1000)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        val splashAnimationCloudGroup1 = AnimationUtils.loadAnimation(this@SplashScreenActivity,
            R.anim.splash_clouds
        )
        splashScreenBinding.imageViewCloud2.animation = splashAnimationCloudGroup1
        splashScreenBinding.imageViewCloud3.animation = splashAnimationCloudGroup1
        splashScreenBinding.imageViewCloud5.animation = splashAnimationCloudGroup1
        splashScreenBinding.imageViewCloud6.animation = splashAnimationCloudGroup1

        splashAnimationCloudGroup1.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                splashScreenBinding.imageViewCloud2.visibility = View.VISIBLE
                splashScreenBinding.imageViewCloud3.visibility = View.VISIBLE
                splashScreenBinding.imageViewCloud5.visibility = View.VISIBLE
                splashScreenBinding.imageViewCloud6.visibility = View.VISIBLE

                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 1000)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        val splashAnimationCloudGroup2 = AnimationUtils.loadAnimation(this@SplashScreenActivity,
            R.anim.splash_clouds_2
        )
        splashScreenBinding.imageViewCloud1.animation = splashAnimationCloudGroup2
        splashScreenBinding.imageViewCloud4.animation = splashAnimationCloudGroup2
        splashScreenBinding.imageViewCloud7.animation = splashAnimationCloudGroup2

        splashAnimationCloudGroup2.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                splashScreenBinding.imageViewCloud1.visibility = View.VISIBLE
                splashScreenBinding.imageViewCloud4.visibility = View.VISIBLE
                splashScreenBinding.imageViewCloud7.visibility = View.VISIBLE

                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 1000)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }
}