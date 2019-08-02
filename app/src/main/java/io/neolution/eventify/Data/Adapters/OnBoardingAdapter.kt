package io.neolution.eventify.Data.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

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