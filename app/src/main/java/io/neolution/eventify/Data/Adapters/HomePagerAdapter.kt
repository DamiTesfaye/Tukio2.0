package io.neolution.eventify.Data.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class HomePagerAdapter(manager : FragmentManager, val fragmentlist: List<Fragment>): FragmentPagerAdapter(manager) {
    override fun getCount(): Int {
        return fragmentlist.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentlist[position]
    }

}