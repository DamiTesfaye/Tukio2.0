package io.neolution.eventify.View.Fragments.OnBoardingFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import io.neolution.eventify.R

class UpdatesFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.updates_onboarding, container, false)
        val updateImage = view.findViewById<ImageView>(R.id.updates_image)
        Glide.with(context!!)
            .load(R.drawable.updateii)
            .into(updateImage)

        return view
    }

}