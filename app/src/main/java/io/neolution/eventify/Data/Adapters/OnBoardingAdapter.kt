package io.neolution.eventify.Data.Adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class OnBoardingAdapter(private val listFragment: List<Fragment>, manager: FragmentManager): FragmentPagerAdapter(manager) {

    override fun getItem(position: Int): Fragment {
        return listFragment[position]
    }

    override fun getCount(): Int {
        return listFragment.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position){
            0 -> "Posted Events"
            1 -> "Promoted Events"
            else -> {
              ""
            }
        }
    }
}