package com.exceptos.tukio.View.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.WindowManager
import com.exceptos.tukio.Data.ModelClasses.indicate
import com.exceptos.tukio.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val window = window
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        about_contact_us.setOnClickListener {
            val mailIntent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto: " + "tukiomail@gmail.com"))
            mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your Subject")
            mailIntent.putExtra(Intent.EXTRA_TEXT, "Your text")

            try {
                startActivity(mailIntent)
            }
            catch (e: Exception){
                indicate("Sorry, you have no mailing app")
            }

        }

        about_rate_us.setOnClickListener {
            //TODO: INTENT TO START THE PAGE ON PLAYSTORE
        }

        about_share.setOnClickListener {

            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND

            val finalText = "Do you know you can now find out about events happening in your area that match your interests? \n \n  \n " +
                    "Download Tukio now to do exactly just that! @ https://tinyurl.com/DownloadTukio"

            shareIntent.putExtra(Intent.EXTRA_TEXT, finalText)
            shareIntent.type = "text/plain"
            startActivity(Intent.createChooser(shareIntent, "Share Event!!"))
        }

    }
}
