package org.codesolo.manservapp.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import org.codesolo.manservapp.fragment.*

class PlacePageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                PlaceBeforeFragment()
            }
            1 -> PlaceDuringFragment()
            else -> {
                return PlaceAfterFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Before"
            1 -> "During"
            else -> {
                return "After"
            }
        }
    }
}