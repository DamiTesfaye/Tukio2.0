package io.neolution.eventify.View.Fragments.OnBoardingFragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.neolution.eventify.R

class PersonalisedFeedFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.personalised_onboarding, container, false)
    }

}