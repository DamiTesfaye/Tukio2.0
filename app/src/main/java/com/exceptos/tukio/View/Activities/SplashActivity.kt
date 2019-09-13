package com.exceptos.tukio.View.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.exceptos.tukio.R
import com.exceptos.tukio.Repos.AuthRepo


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val sharedPrefs = getSharedPreferences("startup_pref", Context.MODE_PRIVATE)
        val authInstance = AuthRepo.getAuthInstance()

        if (sharedPrefs.getString("startup", "") !=  "started" && authInstance.currentUser == null){
            Handler().postDelayed(
                {
                    startActivity(Intent(this, OnBoardingActivity::class.java))
                    finish()
                }, 1500
            )
        }else if (sharedPrefs.getString("startup", "") ==  "started" && authInstance.currentUser == null){
            Handler().postDelayed(
                {
                    startActivity(Intent(this, AuthActivity::class.java))
                    finish()
                }, 1500
            )
        }else if (sharedPrefs.getString("startup", "") ==  "started" && authInstance.currentUser != null){
            Handler().postDelayed(
                {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }, 1500
            )
        }
    }
}
