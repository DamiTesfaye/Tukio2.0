package io.neolution.eventify.View.Fragments.OnBoardingFragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import io.neolution.eventify.R

class NotificationFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.notification_onboarding, container, false)
        val notificationImage = view.findViewById<ImageView>(R.id.notification_image)
        Glide.with(context!!)
            .load(R.drawable.reminder)
            .into(notificationImage)
        return view
    }

}