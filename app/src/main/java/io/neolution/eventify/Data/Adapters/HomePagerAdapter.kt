package io.neolution.eventify.Data.Adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

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