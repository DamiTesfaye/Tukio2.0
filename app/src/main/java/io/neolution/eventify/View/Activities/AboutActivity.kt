package io.neolution.eventify.View.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.WindowManager
import io.neolution.eventify.Data.ModelClasses.indicate
import io.neolution.eventify.R
import kotlinx.android.synthetic.main.activity_about.*
import java.util.concurrent.TimeUnit

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
            //TODO: SHARE A TEXT INVITING PEOPLE TO DOWNLOAD THIS APP WITH A LINK TO GOOGLE PLAY AND THE WEBSITE
        }

    }
}
