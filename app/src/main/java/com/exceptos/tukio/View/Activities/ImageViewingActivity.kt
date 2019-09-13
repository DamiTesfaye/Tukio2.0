package com.exceptos.tukio.View.Activities

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.exceptos.tukio.R

class ImageViewingActivity : AppCompatActivity() {

    private lateinit var backBtn: ImageButton
    private lateinit var eventImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewing)

        backBtn = findViewById(R.id.view_image_back)
        eventImageView = findViewById(R.id.view_image_image)

        backBtn.setOnClickListener {
            finish()
        }

        val bundle = intent.extras
        val eventImage = bundle!!.getString("eventImage")!!
        val eventImageThumb = bundle.getString("eventImageThumb")!!

        val requestOptions = RequestOptions()
        requestOptions.placeholder(ContextCompat.getDrawable(this, R.drawable.placeholder_2))

        val thumbNailRequest = Glide.with(this).load(eventImageThumb)
        Glide.with(this.applicationContext)
            .setDefaultRequestOptions(requestOptions)
            .asDrawable()
            .load(eventImage)
            .thumbnail(thumbNailRequest)
            .into(eventImageView)
    }
}
